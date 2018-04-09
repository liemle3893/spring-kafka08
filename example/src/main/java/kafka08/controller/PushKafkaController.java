package kafka08.controller;

import com.github.javafaker.Faker;
import com.sk.zk_kafka.producer.KafkaProducerFactory;
import com.sk.zk_kafka.producer.ProducerWrapper;
import kafka.producer.KeyedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/")
public class PushKafkaController {
    Faker faker = new Faker();

    @Autowired
    @Qualifier("byteArray")
    KafkaProducerFactory<byte[], byte[]> byteArrayProducerFactory;
    @Autowired
    @Qualifier("object")
    KafkaProducerFactory<Integer, String> objectProducerFactory;

    ProducerWrapper<byte[], byte[]> byteArrayProducer;
    ProducerWrapper<Integer, String> objectProducer;

    @PostConstruct
    void init() {
        byteArrayProducer = this.byteArrayProducerFactory.createProducer();
        objectProducer = this.objectProducerFactory.createProducer();
    }

    @GetMapping
    ResponseEntity<?> pushKafkaMessage(@RequestParam(name = "message", required = false) String message) {
        String aName = StringUtils.isEmpty(message) ? faker.name().fullName() : message;
        int i = ThreadLocalRandom.current().nextInt(2);
        String topic = "local.test_kafka08-" + i;
        byteArrayProducer.send(Arrays.asList(new KeyedMessage<>("local.test_kafka08-" + i,
                null,
                (aName + "\tbyteArray").getBytes(StandardCharsets.UTF_8))));
        objectProducer.send(topic, i, aName + "\t" + "object");
        return ResponseEntity.ok(aName + "\t:" + i);
    }
}
