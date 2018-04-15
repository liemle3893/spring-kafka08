package com.github.liemle3893.spring_kafka08;

import org.springframework.core.NestedRuntimeException;

/**
 * Base exception for all exception in package.
 */
public class KafkaException extends NestedRuntimeException {
    public KafkaException(String msg) {
        super(msg);
    }

    public KafkaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
