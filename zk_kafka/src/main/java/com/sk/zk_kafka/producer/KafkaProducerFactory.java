package com.sk.zk_kafka.producer;

public interface KafkaProducerFactory<K, V> {
    ProducerWrapper<K, V> createProducer();
}
