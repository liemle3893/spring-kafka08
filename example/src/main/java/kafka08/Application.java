package kafka08;

import com.sk.zk_kafka.annotation.EnableKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafka
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
