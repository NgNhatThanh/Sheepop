package com.app.bdc_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BdcBackendApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(BdcBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
