package com.github.liemle3893.spring_kafka08.listener;

import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class ConsumerWrapper<K, V> implements Runnable {
    private final Log log = LogFactory.getLog(getClass());
    private final String topic;
    private final KafkaStream<K, V> stream;
    private final Method method;
    private final Object bean;
    private final GenericErrorHandler<Throwable, K, V> errorHandler;


    @java.beans.ConstructorProperties({"topic", "stream", "method", "bean"})
    public ConsumerWrapper(String topic,
                           KafkaStream<K, V> stream,
                           Method method,
                           Object bean,
                           GenericErrorHandler<Throwable, K, V> errorHandler) {
        Assert.notNull(topic, "topic must not be null");
        Assert.notNull(stream, "stream must not be null");
        Assert.notNull(method, "method must not be null");
        Assert.notNull(bean, "bean must not be null");
        Assert.notNull(errorHandler, "errorHandler must not be null");
        this.errorHandler = errorHandler;
        this.topic = topic;
        this.stream = stream;
        this.method = method;
        this.bean = bean;
    }

    @Override
    public void run() {
        if (stream == null) {
            throw new NullPointerException("Kafka stream is null");
        }
        log.info("Start polling data at " + Thread.currentThread().getName() + ". Topic:  " + topic);
        while (stream.iterator().hasNext()) {
            MessageAndMetadata<K, V> messageAndMetadata = stream.iterator().next();
            try {
                try {
                    method.invoke(bean, messageAndMetadata);
                } catch (Throwable ex) {
                    errorHandler.handleException(ex, messageAndMetadata);
                }
            } catch (Throwable ex) {
                log.error("Invoke handler method error: " + ex.getLocalizedMessage(), ex);
            }
        }
        log.info("Stop polling data at " + Thread.currentThread().getName() + ". Topic:  " + topic);
    }
}