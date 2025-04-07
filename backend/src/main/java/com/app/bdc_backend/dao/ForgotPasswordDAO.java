package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.ForgotPasswordToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordDAO extends MongoRepository<ForgotPasswordToken, String> {
    ForgotPasswordToken findByToken(String token);
}
