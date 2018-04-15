package com.github.liemle3893.spring_kafka08.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KafkaListeners {
    KafkaListener[] value();
}
