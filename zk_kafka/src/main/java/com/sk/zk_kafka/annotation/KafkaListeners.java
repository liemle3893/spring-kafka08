package com.sk.zk_kafka.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KafkaListeners {
    KafkaListener[] value();
}
