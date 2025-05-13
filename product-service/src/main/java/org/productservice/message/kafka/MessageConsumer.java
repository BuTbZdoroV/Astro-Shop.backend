package org.productservice.message.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    
    public void listen(Object message) {
        logger.info("Received message: {}", message);
        // Обработка данных
    }
}