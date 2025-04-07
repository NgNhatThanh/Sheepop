package com.app.bdc_backend.service.redis;

public interface JwtRedisService {

    boolean isRefreshTokenValid(String username, String token);

    void setNewRefreshToken(String username, String token);

    void deleteRefreshToken(String username);

}
