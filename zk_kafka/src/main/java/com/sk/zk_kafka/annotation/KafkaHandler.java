package com.sk.zk_kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method must have sign of methodName({@link Throwable},  {@link kafka.message.MessageAndMetadata})
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaHandler {
}
