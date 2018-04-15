package com.github.liemle3893.spring_kafka08.config;

public abstract class KafkaListenerConfigUtils {

    public static final String ZK_CONNECT_STR_PROP = "spring.kafka.kafka08.zk-connect";
    public static final String AUTO_OFFSET_PROP = "spring.kafka.kafka08.consumer.auto.offset.reset";

    /**
     * The bean name of the internally managed Kafka listener annotation processor.
     */
    public static final String KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME =
            "vn.com.vng.wpl.kafka.config.internalKafkaListenerAnnotationProcessor";
    public static final String KAFKA_LISTENER_GENERAL_CONFIGURATION =
            "vn.com.vng.wpl.kafka.config.internalKafkaListenerConfiguration";
}
