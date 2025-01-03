package com.tarento.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.order.dto.OrchestratorResponseDTO;
import com.tarento.order.service.UpdateService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final UpdateService update;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(UpdateService update, ObjectMapper objectMapper) {
        this.update = update;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-updated", groupId = "my-group")
    public void listen(String message) {
        try {
            OrchestratorResponseDTO responseDTO = objectMapper.readValue(message, OrchestratorResponseDTO.class);
            log.info("Received message from Kafka: {} ", responseDTO);
            update.updateOrder(responseDTO);
        } catch (Exception e) {
            log.error("Failed to process message: {} ", e.getMessage());
        }
    }
}
