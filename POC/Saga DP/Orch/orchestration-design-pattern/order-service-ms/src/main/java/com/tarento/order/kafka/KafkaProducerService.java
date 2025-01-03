package com.tarento.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }


    /**
     * Sends a message to the specified Kafka topic.
     *
     * @param topic   the Kafka topic to which the message is sent
     * @param key     the key for the message, used for partitioning
     * @param message the actual message to send
     */
    public void sendMessage(String topic, String key, Object message) {
        try {
            String data = mapper.writeValueAsString(message);
            kafkaTemplate.send(topic, data);
            log.info("Message sent to Kafka topic: {}", topic);
        } catch (Exception ex) {
            log.error("An unexpected error occurred while sending message [{}] to topic: {}. Error: {}",
                    message, topic, ex.getMessage(), ex);
        }
    }
}
