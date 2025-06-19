package com.technokratos.config;

import com.technokratos.dto.kafkaMessage.LocationUpdateMessage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaMockConfig {

    @Bean
    public KafkaTemplate<String, LocationUpdateMessage> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}