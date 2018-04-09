package com.sk.zk_kafka.core;

import com.sk.zk_kafka.KafkaException;
import com.sk.zk_kafka.annotation.KafkaListener;
import com.sk.zk_kafka.annotation.KafkaListeners;
import com.sk.zk_kafka.listener.ConsumerEndpoint;
import com.sk.zk_kafka.listener.ErrorHandlerAdapter;
import com.sk.zk_kafka.listener.GenericErrorHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;


public class KafkaListenerAnnotationBeanPostProcessor<K, V>
        implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {
    private final Log log = LogFactory.getLog(getClass());
    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, Set<KafkaListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    new MethodIntrospector.MetadataLookup<Set<KafkaListener>>() {

                        @Override
                        public Set<KafkaListener> inspect(Method method) {
                            Set<KafkaListener> listenerMethods = findListenerAnnotations(method);
                            return (!listenerMethods.isEmpty() ? listenerMethods : null);
                        }

                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
                if (this.log.isTraceEnabled()) {
                    this.log.trace("No @KafkaListener annotations found on bean type: " + bean.getClass());
                }
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<KafkaListener>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    for (KafkaListener listener : entry.getValue()) {
                        GenericErrorHandler errorHandler = createErrorHandler(
                                bean,
                                listener.errorHandler()
                        );
                        processKafkaListener(listener, method, bean, errorHandler);
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug(annotatedMethods.size() + " @KafkaListener methods processed on bean '"
                            + beanName + "': " + annotatedMethods);
                }
            }

        }
        return bean;
    }

    private GenericErrorHandler createErrorHandler(Object targetBean,
                                                   String methodName) {
        return new ErrorHandlerAdapter(targetBean, methodName);
    }

    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    private Set<KafkaListener> findListenerAnnotations(Method method) {
        Set<KafkaListener> listeners = new HashSet<KafkaListener>();
        KafkaListener ann = AnnotationUtils.findAnnotation(method, KafkaListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        KafkaListeners anns = AnnotationUtils.findAnnotation(method, KafkaListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.asList(anns.value()));
        }
        return listeners;
    }

    protected void processKafkaListener(final KafkaListener kafkaListener,
                                        Method method, Object bean,
                                        GenericErrorHandler errorHandler) {
        Method methodToUse = checkProxy(method, bean);

        try {
            Class<? extends Deserializer> keyDecoderClass = kafkaListener.keyDecoder();
            Class<? extends Deserializer> valueDecoderClass = kafkaListener.valueDecoder();
            Deserializer keyDecoder = keyDecoderClass.newInstance();
            Deserializer valueDecoder = valueDecoderClass.newInstance();

            Executors.newSingleThreadExecutor().submit(() -> {
                ConsumerEndpoint consumerEndpoint = new ConsumerEndpoint(
                        Arrays.asList(kafkaListener.topics()),// topics
                        kafkaListener.topicPattern(), // rawTopicPattern
                        kafkaListener.groupId(), // groupId
                        kafkaListener.configBeanName(), // configBeanRef
                        kafkaListener.threadNum(), // threadNum
                        methodToUse, // method
                        bean, // bean
                        errorHandler,
                        keyDecoder,
                        valueDecoder
                );
                consumerEndpoint.setBeanFactory(beanFactory);
                consumerEndpoint.run();
            });
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new KafkaException("Cannot create decoder.", ex);
        }

    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @KafkaListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException noMethod) {
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@KafkaListener method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


}
