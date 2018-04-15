package com.github.liemle3893.spring_kafka08.config;

import com.github.liemle3893.spring_kafka08.KafkaException;

public class MissingListenerConfig extends KafkaException {
    public MissingListenerConfig(String msg) {
        super(msg);
    }

    public MissingListenerConfig(String msg, Throwable cause) {
        super(msg, cause);
    }
}
