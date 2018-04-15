package com.github.liemle3893.spring_kafka08.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProducerWrapper<K, V> {

    private final Producer<byte[], byte[]> delegate;
    private final Serializer keySerializer;
    private final Serializer valueSerializer;

    public ProducerWrapper(Producer<byte[], byte[]> producer,
                           Serializer<K> keySerializer,
                           Serializer<V> valueSerializer) {
        this.delegate = producer;
        this.keySerializer = keySerializer == null ? new ByteArraySerializer() : keySerializer;
        this.valueSerializer = valueSerializer == null ? new ByteArraySerializer() : valueSerializer;
    }


    public void send(String topic, K key, V value) {
        send(new KeyedMessage<>(topic,
                keySerializer.serialize(topic, key),
                valueSerializer.serialize(topic, value))
        );
    }

    public void send(String topic, V value) {
        send(topic, null, value);
    }

    public void send(String topic, List<Map.Entry<K, V>> messages) {
        if (messages == null) {
            // No-OP
        } else {
            List<KeyedMessage<byte[], byte[]>> keyedMessages = messages.stream()
                    .map(msg -> new KeyedMessage<>(topic, keySerializer.serialize(topic, msg.getKey()),
                            valueSerializer.serialize(topic, msg.getValue())))
                    .collect(Collectors.toList());
            send(keyedMessages);
        }
    }

    public void send(KeyedMessage<byte[], byte[]> keyedMessage) {
        if (keyedMessage == null) {
            // No-OP
        } else {
            delegate.send(keyedMessage);
        }
    }

    public void send(List<KeyedMessage<byte[], byte[]>> keyedMessages) {
        if (keyedMessages == null || keyedMessages.isEmpty()) {
            // No-OP
        } else {
            delegate.send(keyedMessages);
        }
    }

    public void close() {
        delegate.close();
    }

}
