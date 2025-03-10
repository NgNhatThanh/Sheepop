package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.ProductFacadeService;
import com.app.bdc_backend.model.dto.request.SelectVariationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductFacadeService productFacadeService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") String productId) {
        return ResponseEntity.ok(productFacadeService.getProduct(productId, false));
    }

    @PostMapping("/select_variation")
    public ResponseEntity<?> selectVariation(@RequestBody SelectVariationDTO selectVariationDTO) {
        return ResponseEntity.ok(productFacadeService.selectVariation(selectVariationDTO));
    }

}
