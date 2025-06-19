package com.technokratos;

import com.technokratos.config.KafkaMockConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = KafkaMockConfig.class)
@ActiveProfiles(profiles = "test")
@Testcontainers
public class LocationMigrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "%s/location".formatted(mongoDBContainer.getConnectionString()));
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(mongoTemplate);
    }

    @Test
    public void testMigrationWithMongoContainer() {
        assertTrue(mongoTemplate.collectionExists("location"), "Коллекция должна быть создана миграцией");
    }
}
