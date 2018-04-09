package com.sk.zk_kafka.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(KafkaBootstrapConfiguration.class)
public @interface EnableKafka {
}
