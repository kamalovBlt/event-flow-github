package com.technokratos.repository.impl;

import com.technokratos.repository.api.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken);
    }

    @Override
    public String findByEmail(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void delete(String email) {
        redisTemplate.delete(email);
    }

}
