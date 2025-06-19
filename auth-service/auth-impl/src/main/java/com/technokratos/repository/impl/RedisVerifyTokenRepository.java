package com.technokratos.repository.impl;

import com.technokratos.repository.api.VerifyCodeRepository;
import com.technokratos.service.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisVerifyTokenRepository implements VerifyCodeRepository {

    private static final String KEY_TEMPLATE = "verify_code:%s";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void save(String email, String code) {
        redisTemplate.opsForValue().set(KEY_TEMPLATE.formatted(email), code, Duration.ofMillis(jwtProperties.getVerifyCodeValidity()));
    }

    @Override
    public String findByEmail(String email) {
        return redisTemplate.opsForValue().get(KEY_TEMPLATE.formatted(email));
    }

}
