package com.app.bdc_backend.service.redis.impl;

import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@AllArgsConstructor
public class CartRedisService{

    private RedisTemplate<String, Object> redisTemplate;

    private final Duration expireTime = Duration.ofMinutes(30);

    public Cart findByUser(String username){
        try{
            Cart cart = (Cart)redisTemplate.opsForValue().get(username + "-cart");
            return cart;
        }
        catch (Exception e){
            log.error("Redis didn't start");
            return null;
        }
    }

    public void save(Cart cart){
        try {
            redisTemplate.opsForValue().set(cart.getUser().getUsername() + "-cart", cart, expireTime);
            log.info("Save cart: " + cart.getUser().getUsername());
        }
        catch (Exception e){
            log.error("Redis didn't start");
        }
    }

}
