package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.product.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping(){
        return ResponseEntity.ok().build();
    }

}
