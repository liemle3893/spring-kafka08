package com.github.liemle3893.spring_kafka08.annotation;

import com.github.liemle3893.spring_kafka08.config.KafkaListenerConfigUtils;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(KafkaListeners.class)
public @interface KafkaListener {

    /**
     * Topics that consumer will listen on.
     *
     * @return
     */
    String[] topics() default {};

    /**
     * Use either {@link KafkaListener#topicPattern()} or {@link KafkaListener#topics()}.
     * {@link KafkaListener#topicPattern()} will be used if both exists.
     *
     * @return
     */
    String topicPattern() default "";

    /**
     * Will handle the error.
     *
     * @return
     */
    String errorHandler() default "";

    String groupId() default "";

    int threadNum() default 1;

    String configBeanName() default KafkaListenerConfigUtils.KAFKA_LISTENER_GENERAL_CONFIGURATION;

    Class<? extends Deserializer> keyDecoder() default ByteArrayDeserializer.class;

    Class<? extends Deserializer> valueDecoder() default ByteArrayDeserializer.class;

}
