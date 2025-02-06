package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.CategoryRepository;
import com.app.bdc_backend.model.product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public List<Category> searchByName(String keyword){
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Category findById(String id){
        return categoryRepository.findById(id).orElse(null);
    }

}
