package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.product.CategoryRepository;
import com.app.bdc_backend.model.product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public void saveAll(List<Category> categories){
        categoryRepository.saveAll(categories);
    }

    public List<Category> searchByName(String keyword){
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    public void increaseProductCount(Category category){
        List<Category> updatedCats = new ArrayList<>();
        while(category != null){
            category.setProductCount(category.getProductCount() + 1);
            updatedCats.add(category);
            category = category.getParent();
        }
        saveAll(updatedCats);
    }

    public Category findById(String id){
        return categoryRepository.findById(id).orElse(null);
    }

    public int countByParent(Category parent){
        return categoryRepository.countByParent(parent);
    }

}
