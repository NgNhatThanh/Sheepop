package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
public class CommonController {

    private final CategoryService categoryService;

    @GetMapping("/category/search")
    public ResponseEntity<?> searchCategoryByName(@RequestParam String keyword) {
        return ResponseEntity.ok().body(Map.of(
                "data", categoryService.searchByName(keyword)
        ));
    }

}
