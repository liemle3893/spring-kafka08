Kafka: 0.8.2.2

Scala: 2.11

Very similar use with: [https://github.com/spring-projects/spring-kafka](spring-kafka)

### Why?

At my work, my colleges use Kafka 0.8 broker ("WTF?!!", you ask)

I'm on my way to reconstruct all of our services, but I got stuck when working with Spring Boot Kafka.
Spring Boot support Kafka 0.10 and above (my SB version it 1.5.8) and our ecosystem reply heavily on Kafka so changing it is so risky.

### How to use?

#### Maven
```xml
<project>
<repositories>
        <repository>
          <snapshots>
            <enabled>
              false
            </enabled>
          </snapshots>
          <id>
            bintray-liemle3893-Personal
          </id>
          <name>
            bintray
          </name>
          <url>
            https://dl.bintray.com/liemle3893/Personal
          </url>
        </repository>
      </repositories>
<dependency>
  <groupId>com.github.liemle3893</groupId>
  <artifactId>spring-kafka08</artifactId>
  <version>0.2.0</version>
  <type>pom</type>
</dependency>
</project>

```

#### Gradle

```groovy

repositories {
	maven {
		url  "https://dl.bintray.com/liemle3893/Personal"
	}
}

compile 'com.github.liemle3893:spring-kafka08:0.2.0'
```

#### Code

```java
@Component
@Slf4j
public class Consumer {

	@KafkaListener(
			topicPattern = "${app.kafka.test.topic_pattern}",
			groupId = "${app.kafka.test.consumer.group_id}",
			threadNum = "${app.kafka.test.consumer.worker:1}",
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

```

### Configuration

```yaml
spring.kafka.kafka08.zk-connect: localhost:2181/kafka
spring.kafka.kafka08.brokers: localhost:9092
spring.kafka.kafka08.consumer.auto.offset.reset: largest
```
