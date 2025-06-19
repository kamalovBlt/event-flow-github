package com.technokratos.repository.impl;

import com.technokratos.repository.api.RefreshTokenRepository;
import com.technokratos.service.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String KEY_TEMPLATE = "refresh_token:%s";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(KEY_TEMPLATE.formatted(email), refreshToken, Duration.ofMillis(jwtProperties.getRefreshTokenValidity()));
    }

    @Override
    public String findByEmail(String email) {
        return redisTemplate.opsForValue().get(KEY_TEMPLATE.formatted(email));
    }

    @Override
    public void delete(String email) {
        redisTemplate.delete(KEY_TEMPLATE.formatted(email));
    }

}
