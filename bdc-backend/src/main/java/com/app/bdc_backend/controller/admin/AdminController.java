package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

}
