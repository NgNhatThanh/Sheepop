package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.facade.AdminCategoryFacadeService;
import com.app.bdc_backend.model.dto.request.AddCategoryDTO;
import com.app.bdc_backend.model.dto.request.UpdateCategoryDTO;
import com.app.bdc_backend.model.dto.response.CategoryDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/category")
public class CategoryController {

    private final AdminCategoryFacadeService adminCategoryFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getCategoryList(@RequestParam(defaultValue = "") String keyword,
                                             @RequestParam int sortType,
                                             @RequestParam int page,
                                             @RequestParam int limit){
        Page<CategoryDTO> dtos = adminCategoryFacadeService.getCategories(keyword, sortType, page, limit);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }

    @GetMapping("/get_sub_categories")
    public ResponseEntity<?> getSubCategories(@RequestParam String parentId){
        return ResponseEntity.ok(adminCategoryFacadeService.getSubCategories(parentId));
    }

    @PostMapping("/update/{catId}")
    public ResponseEntity<?> updateCategory(@PathVariable String catId,
                                            @RequestBody UpdateCategoryDTO dto){
        CategoryDTO updatedCat = adminCategoryFacadeService.update(catId, dto);
        return ResponseEntity.ok().body(updatedCat);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody AddCategoryDTO dto){
        CategoryDTO category = adminCategoryFacadeService.addCategory(dto);
        return ResponseEntity.ok().body(category);
    }

}
