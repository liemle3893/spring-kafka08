package com.sk.zk_kafka.listener;

import kafka.message.MessageAndMetadata;

public interface GenericErrorHandler<E extends Throwable, K, V> {
    void handleException(E ex, MessageAndMetadata<K, V> message);
}
