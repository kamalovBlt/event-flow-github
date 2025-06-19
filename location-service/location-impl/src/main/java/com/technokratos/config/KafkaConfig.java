package com.technokratos.config;

import com.technokratos.dto.kafkaMessage.LocationUpdateMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile(value = {"local", "dev", "prod"})
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, LocationUpdateMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "loc-service-tx-1");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, LocationUpdateMessage> kafkaTemplate() {
        KafkaTemplate<String, LocationUpdateMessage> template = new KafkaTemplate<>(producerFactory());
        template.setTransactionIdPrefix("loc-service-tx-");
        return template;
    }

    @Bean
    public KafkaTransactionManager<String, LocationUpdateMessage> kafkaTransactionManager(ProducerFactory<String, LocationUpdateMessage> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public NewTopic updateSeatsTopic() {
        return new NewTopic("location-update", 1, (short) 1);
    }
}
