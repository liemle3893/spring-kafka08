package com.sk.zk_kafka.config;

import com.sk.zk_kafka.KafkaException;

public class MissingListenerConfig extends KafkaException {
    public MissingListenerConfig(String msg) {
        super(msg);
    }

    public MissingListenerConfig(String msg, Throwable cause) {
        super(msg, cause);
    }
}
