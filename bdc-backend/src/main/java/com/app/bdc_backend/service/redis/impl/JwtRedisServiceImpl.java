package com.app.bdc_backend.service.redis.impl;

import com.app.bdc_backend.service.JwtService;
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

    private JwtService jwtService;

    private final String REFRESH_TOKEN_ID_NAME = "refresh_token_id";

    @Override
    public boolean isRefreshTokenValid(String username, String token) {
        String tokenId = jwtService.extractId(token);
        String curTokenId = (String)redisTemplate.opsForHash().get(username, REFRESH_TOKEN_ID_NAME);
        return tokenId.equals(curTokenId);
    }

    @Override
    public void setNewRefreshToken(String username, String token) {
        String tokenId = jwtService.extractId(token);
        redisTemplate.opsForHash().put(username, REFRESH_TOKEN_ID_NAME, tokenId);
    }

    @Override
    public void deleteRefreshToken(String username) {
        redisTemplate.opsForHash().delete(username, REFRESH_TOKEN_ID_NAME);
    }


}
