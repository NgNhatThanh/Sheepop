package com.app.bdc_backend.service.redis.impl;

import com.app.bdc_backend.service.redis.JwtRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class JwtRedisServiceImpl implements JwtRedisService {

    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean isRefreshTokenValid(String username, String token) {
        String curToken = (String)redisTemplate.opsForHash().get(username, "refresh_token");
        return curToken == null || token.equals(curToken);
    }

    @Override
    public void setNewRefreshToken(String username, String token) {
        redisTemplate.opsForHash().put(username, "refresh_token", token);
    }

    @Override
    public void deleteRefreshToken(String username) {
        redisTemplate.opsForHash().delete(username, "refresh_token");
    }


}
