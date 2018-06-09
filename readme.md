---
typora-root-url: D:\找工作资料\专业资料\java-basic-knowledge\graph
---

## spring boot2整合kafka遇到Exception thrown when sending a message with key='null'问题

1. 最近在学习spring boot2和kafka。就用学着使用spring boot2与kafka集成。项目环境

- 开发工具：IDEA
- spring kafka ：2.1.6.RELEASE
- spring boot2：2.0.2.RELEASE
- Apache kafka：2.11-1.0.0

2. 项目目录

![kafka目录结构](/kafka目录结构.png)

3. application.properties文件

   ```yaml
   #kafka server address
   spring.kafka.bootstrap-servers=10.108.208.51:9092
   
   # Provider
   spring.kafka.producer.retries=0
   spring.kafka.producer.batch-size=16384
   # 指生产者的key和value的编码方式
   spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
   spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
   
   # Consumer
   #消费者组
   spring.kafka.consumer.group-id=test-consumer-group
   spring.kafka.consumer.auto-offset-reset=earliest
   # 指定消费者的解码方式
   spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
   spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
   
   # 日志
   spring.output.ansi.enabled=DETECT
   debug=true
   ```

   

4. 生产者配置文件KafkaProviderConfig.java

   ```java
   @Configuration
   @EnableKafka
   public class KafkaProviderConfig {
   
       @Value("${spring.kafka.bootstrap-servers}")
       private String bootstrapServers;
   
       @Value("${spring.kafka.producer.key-serializer}")
       private String keySerializer;
   
       @Value("${spring.kafka.producer.value-serializer}")
       private String valueSerializer;
   
       @Bean
       public Map<String,Object> producerConfig(){
           Map<String,Object> props = new HashMap<>();
           props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
           props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
           props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
           return props;
       }
   
       @Bean
       public ProducerFactory<String,String> producerFactory(){
           return new DefaultKafkaProducerFactory<>(producerConfig());
       }
   
       @Bean
       public KafkaTemplate<String,String> kafkaTemplate(){
           return new KafkaTemplate<>(producerFactory());
       }
   }
   ```

5. 生产者产生消息发送到kafka

   ```java
   @Component
   public class KafkaSender {
       private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
   
       @Autowired
       private KafkaTemplate<String,String> kafkaTemplate;
   
       private Gson gson = new GsonBuilder().create();
   
       //发送消息的方法
       public void send(){
           Message message = new Message();
           message.setId(System.currentTimeMillis());
           message.setMsg(UUID.randomUUID().toString());
           message.setSendTime(new Date());
           logger.info("+++++++++++++++++++ message = {}", gson.toJson(message));
           kafkaTemplate.send("sweetzcc",gson.toJson(message);
       }
   }
   ```

6. 消费者配置

   ```java
   @Configuration
   @EnableKafka
   public class KafkaConsumerConfig {
   
       @Value("${spring.kafka.bootstrap-servers}")
       private String bootstrapServer;
   
       @Value("${spring.kafka.consumer.key-deserializer}")
       private String keySerializer;
   
       @Value("${spring.kafka.consumer.value-deserializer}")
       private String valueSerializer;
   
       @Value("${spring.kafka.consumer.group-id}")
       private String groupId;
   
       @Value("${spring.kafka.consumer.auto-offset-reset}")
       private String autoOffsetReset;
   
       @Bean
       public Map<String,Object> consumerConfig(){
           Map<String,Object> props = new HashMap<>();
           props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
           props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keySerializer);
           props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSerializer);
           props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
           props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
           return props;
       }
   
       @Bean
       public ConsumerFactory<String,String> consumerFactory(){
           return new DefaultKafkaConsumerFactory<>(consumerConfig());
       }
   
       @Bean
       public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory(){
           ConcurrentKafkaListenerContainerFactory<String,String> factory = new ConcurrentKafkaListenerContainerFactory<>();
           factory.setConsumerFactory(consumerFactory());
           return factory;
       }
   }
   ```

7. 消费者

   ```java
   @Component
   public class KafkaReceiver {
       private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);
   
       @KafkaListener(topics = "sweetzcc")
       public void listen(@Payload String message){
           logger.info("received message={}",message);
       }
   }
   ```

8. 运行测试

   ```java
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
   ```

   运行结果：

   ![kafak运行成功](/kafak运行成功.png)

9. 在开发中遇到的问题。

   我在服务上搭建完一个单节点的kafka服务后，在服务器山启动命令行消费者和服务者进行测试是成功的但是总是报如下错误。

   ```java
   2018-06-09 14:04:13.490  INFO 6268 --- [ntainer#0-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-1, groupId=test-consumer-group] Marking the coordinator node2:9092 (id: 2147483646 rack: null) dead
   2018-06-09 14:04:41.305 ERROR 6268 --- [ad | producer-1] o.s.k.support.LoggingProducerListener    : Exception thrown when sending a message with key='null' and payload='{"id":1528524250944,"msg":"056aa258-09ca-49e9-be25-d72383f96e50","sendTime":"Jun 9, 2018 2:04:10 PM"...' to topic sweetzcc:
   
   org.apache.kafka.common.errors.TimeoutException: Expiring 10 record(s) for sweetzcc-0: 30037 ms has passed since batch creation plus linger time
   ```

   网上的回答比较奇怪。说是在服务器上配置配置host和名称相对应。我查看服务器，发现已经配置完成。，有的说重启kafka服务就可以了，但是都不行。后来发现：错误中有：Marking the coordinator node2:9092 (id: 2147483646 rack: null) dead。发现是通过节点名去查找服务器的。但是我在本地的hosts中并没有配置10.108.208.51 node2。配置完成后问题就解决了。