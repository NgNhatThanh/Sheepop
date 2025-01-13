package com.app.bdc_backend;

import com.app.bdc_backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;

@SpringBootApplication
public class BdcBackendApplication{
	public static void main(String[] args) {
		SpringApplication.run(BdcBackendApplication.class, args);
	}
}
