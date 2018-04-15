package com.github.liemle3893.spring_kafka08.listener;

import kafka.message.MessageAndMetadata;

public interface GenericErrorHandler<E extends Throwable, K, V> {
    void handleException(E ex, MessageAndMetadata<K, V> message);
}
