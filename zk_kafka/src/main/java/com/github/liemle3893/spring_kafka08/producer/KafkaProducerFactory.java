package com.github.liemle3893.spring_kafka08.producer;

public interface KafkaProducerFactory<K, V> {
    ProducerWrapper<K, V> createProducer();
}
