package com.app.bdc_backend;

import com.app.bdc_backend.service.JwtService;
import com.app.bdc_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class BdcBackendApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(BdcBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
