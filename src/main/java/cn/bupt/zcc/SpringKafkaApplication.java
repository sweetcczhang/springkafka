package cn.bupt.zcc;

import cn.bupt.zcc.provider.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SpringKafkaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringKafkaApplication.class, args);
	}

	@Autowired
	private KafkaSender kafkaSender;

	@Override
	public void run(String... strings) throws Exception {
		for (int i=0;i<10;i++){
			kafkaSender.send();
		}
	}
}
