package kafka08.messaging.kafka.consumer;

import com.github.liemle3893.spring_kafka08.annotation.KafkaHandler;
import com.github.liemle3893.spring_kafka08.annotation.KafkaListener;
import com.github.liemle3893.spring_kafka08.serde.IntegerSerde;
import kafka.message.MessageAndMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Consumer {

    @KafkaListener(
            topicPattern = "${app.kafka.test.topic_pattern}",
            groupId = "${app.kafka.test.consumer.group_id}",
            threadNum = 1,
            errorHandler = "errorHandler",
            keyDecoder = IntegerSerde.class,
            valueDecoder = StringDeserializer.class
    )
    public void listenMessage(MessageAndMetadata<Integer, String> messageAndMetadata) {

        String topic = messageAndMetadata.topic();
        Integer key = messageAndMetadata.key();
        int partition = messageAndMetadata.partition();
        String message = messageAndMetadata.message();
        log.info("Thread: {}, topic: {}, partition: {}, key: {}, message: {}", new Object[]{
                Thread.currentThread().getName(),
                topic,
                partition,
                key,
                message
        });
        if (message == null || message.startsWith("exception")) {
            throw new IllegalStateException("Lalala");
        }
    }

    @KafkaHandler
    public void errorHandler(
            Throwable ex,
            MessageAndMetadata<Integer, String> messageAndMetadata
    ) {
        log.info("Error caught");
    }
}
