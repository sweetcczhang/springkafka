package cn.bupt.zcc.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Created by 张城城 on 2018/6/8.
 */
@Component
public class KafkaReceiver {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @KafkaListener(topics = "sweetzcc")
    public void listen(@Payload String message){
        logger.info("received message={}",message);
    }
}
