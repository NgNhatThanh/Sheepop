package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.admin.AdminContentFacadeService;
import com.app.bdc_backend.model.dto.request.BannerDTO;
import com.app.bdc_backend.model.dto.request.UpdateDisplayCategoryDTO;
import com.app.bdc_backend.model.homepage.Banner;
import com.app.bdc_backend.model.homepage.DisplayCategory;
import com.app.bdc_backend.model.product.Category;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/content")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminContentController {

    private final AdminContentFacadeService adminContentFacadeService;

    @GetMapping("/get_banners")
    @Operation(
            summary = "Get list of homepage's banners"
    )
    public ResponseEntity<List<Banner>> getBanners() {
        return ResponseEntity.ok(adminContentFacadeService.getBanners());
    }

    @GetMapping("/get_display_categories")
    @Operation(
            summary = "Get list of homepage's display categories"
    )
    public ResponseEntity<List<DisplayCategory>> getDisplayCategories() {
        return ResponseEntity.ok(adminContentFacadeService.getDisplayCategories());
    }

    @PostMapping("/add_banner")
    @Operation(
            summary = "Add new banner"
    )
    public ResponseEntity<Banner> addBanner(@RequestBody @Valid BannerDTO dto) {
        return ResponseEntity.ok(adminContentFacadeService.addBanner(dto));
    }

    @PatchMapping("/update_banner")
    @Operation(
            summary = "Update a banner"
    )
    public ResponseEntity<?> updateBanner(@RequestParam String bannerId,
                                          @RequestBody @Valid BannerDTO dto) {
        return ResponseEntity.ok(adminContentFacadeService.updateBanner(bannerId, dto));
    }

    @GetMapping("/get_parent_cats")
    @Operation(
            summary = "Get list of parent categories"
    )
    public ResponseEntity<List<Category>> getParentCategories() {
        return ResponseEntity.ok(adminContentFacadeService.getParentCategories());
    }

    @PostMapping("/add_display_cat")
    @Operation(
            summary = "Add a display category to homepage"
    )
    public ResponseEntity<DisplayCategory> addDisplayCategory(@RequestParam String categoryId){
        return ResponseEntity.ok(adminContentFacadeService.addDisplayCategory(categoryId));
    }

    @PostMapping("/delete_display_cat")
    @Operation(
            summary = "Delete a display category"
    )
    public ResponseEntity<?> deleteDisplayCategory(@RequestParam String categoryId){
        adminContentFacadeService.deleteDisplayCategory(categoryId);
        return ResponseEntity.ok().body(Map.of(
                "status", "success"
        ));
    }

    @PostMapping("/update_display_cat")
    @Operation(
            summary = "Update a display category"
    )
    public ResponseEntity<DisplayCategory> updateDisplayCat(@RequestParam String disCatId,
                                              @RequestBody @Valid UpdateDisplayCategoryDTO dto){
        return ResponseEntity.ok(adminContentFacadeService.updateDisplayCategory(disCatId, dto));
    }

}
