package com.tarento.saga.config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.saga.common.OrchestratorRequestDTO;
import com.tarento.saga.common.OrchestratorResponseDTO;
import com.tarento.saga.service.OrchestratorService;

import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class OrchestratorConfig {

    private final OrchestratorService orchestratorService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrchestratorConfig(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @Bean
    public Function<Flux<Object>, Flux<OrchestratorResponseDTO>> processor() {
        return flux -> flux
                .map(this::convertToDTO)
                .filter(Objects::nonNull)
                .flatMap(orchestratorService::orderProduct)
                .doOnNext(dto -> log.info("Status : {}", dto.getStatus()))
                .onErrorResume(e -> {
                    log.error("Error processing message: {}", e.getMessage());
                    return Flux.empty();  // Handle error and continue processing
                });
    }

    private OrchestratorRequestDTO convertToDTO(Object rawData) {
        log.info("data type {} ", rawData.getClass().getTypeName());
        log.info("data type {} ", rawData);
        try {
            if (rawData instanceof byte[]) {
                String jsonString = new String((byte[]) rawData, StandardCharsets.UTF_8);
                return objectMapper.readValue(jsonString, OrchestratorRequestDTO.class);
            } else if (rawData instanceof String) {
                return objectMapper.readValue((String) rawData, OrchestratorRequestDTO.class);
            } else {
                log.warn("Unexpected data type: {}. Expected byte[] or String", rawData.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to convert rawData to OrchestratorRequestDTO: {}", e.getMessage());
            return null;
        }
    }
}
