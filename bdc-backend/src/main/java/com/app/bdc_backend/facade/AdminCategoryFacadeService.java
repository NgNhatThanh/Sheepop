package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.request.AddCategoryDTO;
import com.app.bdc_backend.model.dto.request.UpdateCategoryDTO;
import com.app.bdc_backend.model.dto.response.CategoryDTO;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.CategoryService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Category> sortedCategories = new ArrayList<>(rootCat);

        if (sortType >= 3) {
            sortedCategories.sort(Comparator.comparing(Category::getCreatedAt));
            if (sortType == 3) {
                Collections.reverse(sortedCategories);
            }
        } else {
            sortedCategories.sort(Comparator.comparing(Category::getProductCount));
            if (sortType == 1) {
                Collections.reverse(sortedCategories);
            }
        }
        List<CategoryDTO> dtos = sortedCategories.stream()
                .map(this::toCategoryDTO)
                .toList();
        int start = page * limit;
        int end = Math.min(start + limit, dtos.size());
        List<CategoryDTO> pagedList = dtos.subList(start, end);
        return new PageImpl<>(pagedList, PageRequest.of(page, limit), dtos.size());
    }

    public List<CategoryDTO> getSubCategories(String parentId) {
        Category parent = categoryService.findById(parentId);
        if(parent == null)
            throw new RequestException("Invalid request: invalid parent Id");
        if(!parent.isHasChildren())
            throw new RequestException("Invalid request: category does not have any sub categories");
        List<Category> subCats = categoryService.getByParent(parent);
        return subCats.stream().map(this::toCategoryDTO).collect(Collectors.toList());
    }

    public CategoryDTO update(String catId, UpdateCategoryDTO dto){
        if(dto.getName() == null
                || dto.getName().isEmpty()
                || dto.getDescription() == null
                || dto.getDescription().isEmpty())
            throw new RequestException("Invalid request: data mustn't be empty");
        Category category = categoryService.findById(catId);
        if(category == null)
            throw new RequestException("Invalid request: category does not exist");
        List<Category> sameLevelCats = categoryService.getByParent(category.getParent());
        for(Category sameLevelCat : sameLevelCats) {
            if(!sameLevelCat.equals(category)
                    && sameLevelCat.getName().equalsIgnoreCase(dto.getName()))
                throw new RequestException("Invalid request: category already exists in group");
        }
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryService.saveAll(List.of(category));
        return toCategoryDTO(category);
    }

    public CategoryDTO addCategory(AddCategoryDTO dto){
        if(dto.getName() == null
                || dto.getName().isEmpty()
                || dto.getDescription() == null
                || dto.getDescription().isEmpty())
            throw new RequestException("Invalid request: data mustn't be empty");
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(new Date());
        if(dto.getParentId() != null){
            Category parent = categoryService.findById(dto.getParentId());
            if(parent == null)
                throw new RequestException("Invalid request: parent category does not exist");
            category.setParent(parent);
            parent.setHasChildren(true);
            categoryService.save(parent);
        }
        category = categoryService.save(category);
        return toCategoryDTO(category);
    }

    private CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO dto = ModelMapper.getInstance().map( category, CategoryDTO.class);
        dto.setId(category.getId().toString());
        dto.setParentId(category.getParent() != null ? category.getParent().getId().toString() : null);
        return dto;
    }

}
