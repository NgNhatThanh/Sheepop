package com.app.bdc_backend.service.redis.impl;

import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.dto.CartRedisDTO;
import com.app.bdc_backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class CartRedisService{

    private RedisTemplate<String, Object> redisTemplate;

    private final Duration expiretime = Duration.ofMinutes(30);

    public Cart findByUser(User user){
        try{
            Cart cart = (Cart)redisTemplate.opsForValue().get(user.getUsername() + "-cart");
            return cart;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void save(Cart cart){
        CartRedisDTO dto = new CartRedisDTO();
        dto.setId(cart.getId().toString());
        dto.setUsername(cart.getUser().getUsername());
        dto.setUpdatedAt(cart.getUpdatedAt());
        redisTemplate.opsForValue().set(cart.getUser().getUsername() + "-cart", dto, 5, TimeUnit.SECONDS);
        System.out.println(redisTemplate.opsForValue().get(cart.getUser().getUsername() + "-cart"));
        log.info("Save cart: " + cart.getUser().getUsername());
    }

}
