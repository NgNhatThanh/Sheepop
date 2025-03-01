package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.facade.AdminCategoryFacadeService;
import com.app.bdc_backend.model.dto.response.CategoryDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
