package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.facade.admin.AdminContentFacadeService;
import com.app.bdc_backend.model.dto.request.BannerDTO;
import com.app.bdc_backend.model.dto.request.UpdateDisplayCategoryDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/content")
public class AdminContentController {

    private final AdminContentFacadeService adminContentFacadeService;

    @GetMapping("/get_banners")
    public ResponseEntity<?> getBanners() {
        return ResponseEntity.ok(adminContentFacadeService.getBanners());
    }

    @GetMapping("/get_display_categories")
    public ResponseEntity<?> getDisplayCategories() {
        return ResponseEntity.ok(adminContentFacadeService.getDisplayCategories());
    }

    @PostMapping("/add_banner")
    public ResponseEntity<?> addBanner(@RequestBody @Valid BannerDTO dto) {
        return ResponseEntity.ok(adminContentFacadeService.addBanner(dto));
    }

    @PatchMapping("/update_banner")
    public ResponseEntity<?> updateBanner(@RequestParam String bannerId,
                                          @RequestBody @Valid BannerDTO dto) {
        return ResponseEntity.ok(adminContentFacadeService.updateBanner(bannerId, dto));
    }

    @GetMapping("/get_parent_cats")
    public ResponseEntity<?> getParentCategories() {
        return ResponseEntity.ok(adminContentFacadeService.getParentCategories());
    }

    @PostMapping("/add_display_cat")
    public ResponseEntity<?> addDisplayCategory(@RequestParam String categoryId){
        return ResponseEntity.ok(adminContentFacadeService.addDisplayCategory(categoryId));
    }

    @PostMapping("/delete_display_cat")
    public ResponseEntity<?> deleteDisplayCategory(@RequestParam String categoryId){
        adminContentFacadeService.deleteDisplayCategory(categoryId);
        return ResponseEntity.ok().body(Map.of(
                "status", "success"
        ));
    }

    @PostMapping("/update_display_cat")
    public ResponseEntity<?> updateDisplayCat(@RequestParam String disCatId,
                                              @RequestBody @Valid UpdateDisplayCategoryDTO dto){
        return ResponseEntity.ok(adminContentFacadeService.updateDisplayCategory(disCatId, dto));
    }

}
