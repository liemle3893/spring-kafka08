package com.github.liemle3893.spring_kafka08.annotation;

import com.github.liemle3893.spring_kafka08.config.KafkaListenerConfigUtils;
import com.github.liemle3893.spring_kafka08.config.KafkaProducerConfigUtils;
import com.github.liemle3893.spring_kafka08.core.KafkaListenerAnnotationBeanPostProcessor;
import com.github.liemle3893.spring_kafka08.producer.SimplePartitioner;
import com.github.liemle3893.spring_kafka08.util.MutableConfiguration;
import kafka.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Properties;


@Configuration
public class KafkaBootstrapConfiguration {

    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public KafkaListenerAnnotationBeanPostProcessor kafkaListenerAnnotationProcessor() {
        return new KafkaListenerAnnotationBeanPostProcessor();
    }

    @Bean(KafkaListenerConfigUtils.KAFKA_LISTENER_GENERAL_CONFIGURATION)
    /**
     * @see kafka.consumer.ConsumerConfig
     */
    public MutableConfiguration createDefaultConsumerConfig(
            @Value("${" + KafkaListenerConfigUtils.ZK_CONNECT_STR_PROP + ":}") String zkConnect,
            @Value("${" + KafkaListenerConfigUtils.AUTO_OFFSET_PROP + ":largest}") String autoOffset) {
        MutableConfiguration props = new MutableConfiguration();
        props.put("zookeeper.connect", StringUtils.isEmpty(zkConnect) ? null : zkConnect);
        props.put("zookeeper.sync.time.ms", "200");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS, "20000");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffset);
        return props;
    }

    /**
     * @see kafka.producer.ProducerConfig
     */
    @Bean(KafkaProducerConfigUtils.KAFKA_PRODUCER_GENERAL_CONFIGURATION)
    public ProducerConfig createDefaultProducerConfig(
            @Value("${" + KafkaProducerConfigUtils.KAFKA_BROKERS_LIST_PROP + "}") String brokers) {
        Assert.isTrue(!StringUtils.isEmpty(brokers), "Broker list must be provided");
        Properties props = new Properties();
        props.put("metadata.broker.list", brokers);
        props.put("partitioner.class", SimplePartitioner.class.getName());
        return new ProducerConfig(props);
    }
}
