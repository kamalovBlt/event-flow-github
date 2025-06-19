package com.technokratos.repository;

import com.technokratos.config.RedisConfiguration;
import com.technokratos.config.RedisTestConfiguration;
import com.technokratos.repository.impl.RedisRefreshTokenRepository;
import com.technokratos.service.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(classes = {
        RedisRefreshTokenRepository.class,
        RedisTestConfiguration.class,
        RedisConfiguration.class,
        JwtProperties.class,
})
@ActiveProfiles("test")
public class RedisRefreshTokenRepositoryTest {

    static final String REDIS_IMAGE_NAME = "redis:7.0-alpine";
    static final int REDIS_PORT = 6379;
    static final String REDIS_PASSWORD = "password";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE_NAME))
            .withExposedPorts(REDIS_PORT)
            .withEnv("REDIS_PASSWORD", REDIS_PASSWORD)
            .withCommand("redis-server", "--requirepass", REDIS_PASSWORD);

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.password", () -> REDIS_PASSWORD);
    }

    @Autowired
    RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Test
    void shouldSaveNewRefreshToken() {
        String email = "test1@test.com";
        String token = "token1";
        redisRefreshTokenRepository.save(email, token);
        assertEquals(token, redisRefreshTokenRepository.findByEmail(email));
    }

    @Test
    void shouldUpdateExistingRefreshToken() {
        String email = "test2@test.com";
        String oldToken = "oldToken";
        String newToken = "newToken";

        redisRefreshTokenRepository.save(email, oldToken);
        assertEquals(oldToken, redisRefreshTokenRepository.findByEmail(email));

        redisRefreshTokenRepository.save(email, newToken);
        assertEquals(newToken, redisRefreshTokenRepository.findByEmail(email));
    }

    @Test
    void shouldDeleteRefreshToken() {
        String email = "test3@test.com";
        String token = "tokenToDelete";

        redisRefreshTokenRepository.save(email, token);
        assertEquals(token, redisRefreshTokenRepository.findByEmail(email));

        redisRefreshTokenRepository.delete(email);
        assertNull(redisRefreshTokenRepository.findByEmail(email));
    }
}
