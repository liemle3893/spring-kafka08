package kafka08.configuration;

import com.sk.zk_kafka.producer.DefaultKafkaProducerFactory;
import com.sk.zk_kafka.serde.IntegerSerde;
import kafka.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean("byteArray")
    @Autowired
    public DefaultKafkaProducerFactory byteArrayKafkaProducerFactory(
            ProducerConfig producerConfig) {
        return new DefaultKafkaProducerFactory(producerConfig);
    }

    @Bean("object")
    @Autowired
    public DefaultKafkaProducerFactory<Integer, String> objectKafkaProducerFactory(
            ProducerConfig producerConfig) {
        return new DefaultKafkaProducerFactory(producerConfig, new IntegerSerde(), new StringSerializer());
    }
}