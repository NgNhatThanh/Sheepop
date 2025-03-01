package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.response.CategoryDTO;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.CategoryService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminCategoryFacadeService {

    private final CategoryService categoryService;

    public Page<CategoryDTO> getCategories(String keyword,
                                    int sortType,
                                    int page,
                                    int limit) {
        if(sortType > 4)
            throw new RequestException("Invalid sort type");
        List<Category> getByName = categoryService.searchByName(keyword);
        Set<Category> rootCat = new HashSet<>();
        for(Category category : getByName) {
            Category root = category;
            while(root.getParent() != null) root = root.getParent();
            rootCat.add(root);
        }
        if(sortType >= 3){
            Sort sort = Sort.by(sortType == 3 ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "createdAt");
            Pageable pageable = PageRequest.of(page, limit, sort);
            Page<Category> res = new PageImpl<>(rootCat.stream().toList(), pageable, rootCat.size());
            return res.map(this::toCategoryDTO);
        }
        else {
            List<CategoryDTO> dtos = rootCat.stream()
                    .map(this::toCategoryDTO)
                    .toList();
            Sort sort = Sort.by(sortType == 1 ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "productCount");
            Pageable pageable = PageRequest.of(page, limit, sort);
            return new PageImpl<>(dtos, pageable, dtos.size());
        }
    }

    private CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO dto = ModelMapper.getInstance().map( category, CategoryDTO.class);
        dto.setId(category.getId().toString());
        return dto;
    }

}
