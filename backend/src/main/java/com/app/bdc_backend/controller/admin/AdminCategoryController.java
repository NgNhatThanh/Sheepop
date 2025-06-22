package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.admin.AdminCategoryFacadeService;
import com.app.bdc_backend.model.dto.request.AddCategoryDTO;
import com.app.bdc_backend.model.dto.request.UpdateCategoryDTO;
import com.app.bdc_backend.model.dto.response.CategoryDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/category")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminCategoryController {

    private final AdminCategoryFacadeService adminCategoryFacadeService;

    @GetMapping("/get_list")
    @Operation(
            summary = "Get all categories of system"
    )
    public ResponseEntity<PageResponse<CategoryDTO>> getCategoryList(@RequestParam(defaultValue = "") String keyword,
                                             @RequestParam int sortType,
                                             @RequestParam int page,
                                             @RequestParam int limit){
        Page<CategoryDTO> dtos = adminCategoryFacadeService.getCategories(keyword, sortType, page, limit);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }

    @GetMapping("/get_sub_categories")
    @Operation(
            summary = "Get sub-categories of a parent category",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found sub-categories"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Not found parent category",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ResponseEntity.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<CategoryDTO>> getSubCategories(@RequestParam String parentId){
        return ResponseEntity.ok(adminCategoryFacadeService.getSubCategories(parentId));
    }

    @PostMapping("/update/{catId}")
    @Operation(
            summary = "Update a category"
    )
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable String catId,
                                            @RequestBody @Valid UpdateCategoryDTO dto){
        CategoryDTO updatedCat = adminCategoryFacadeService.update(catId, dto);
        return ResponseEntity.ok().body(updatedCat);
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add a category (parent / sub-category)"
    )
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody @Valid AddCategoryDTO dto){
        CategoryDTO category = adminCategoryFacadeService.addCategory(dto);
        return ResponseEntity.ok().body(category);
    }

}
