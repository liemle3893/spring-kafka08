package com.sk.zk_kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;


public class DefaultKafkaProducerFactory<K, V> implements KafkaProducerFactory<K, V>, DisposableBean, Lifecycle {
    private final Log log = LogFactory.getLog(getClass());
    private final ProducerConfig configs;

    private volatile ProducerWrapper<K, V> producer;

    private Serializer<K> keySerializer;

    private Serializer<V> valueSerializer;

    private volatile boolean running;

    public DefaultKafkaProducerFactory(ProducerConfig configs) {
        this(configs, null, null);
    }

    public DefaultKafkaProducerFactory(ProducerConfig configs,
                                       Serializer<K> keySerializer,
                                       Serializer<V> valueSerializer) {
        this.configs = configs;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public void setKeySerializer(Serializer<K> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    @Override
    public void destroy() throws Exception {
        ProducerWrapper<K, V> producer = this.producer;
        this.producer = null;
        if (producer != null) {
            producer.close();
        }
    }


    @Override
    public void start() {
        this.running = true;
    }


    @Override
    public void stop() {
        try {
            destroy();
        } catch (Exception e) {
            log.error("Exception while stopping producer", e);
        }
    }


    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public ProducerWrapper<K, V> createProducer() {
        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    this.producer = createKafkaProducer();
                }
            }
        }
        return this.producer;
    }

    protected ProducerWrapper<K, V> createKafkaProducer() {
        return new ProducerWrapper<>(new Producer<>(this.configs), keySerializer, valueSerializer);
    }
}
