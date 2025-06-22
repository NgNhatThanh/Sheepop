package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.facade.ProductFacadeService;
import com.app.bdc_backend.model.dto.request.SelectVariationDTO;
import com.app.bdc_backend.model.dto.response.ProductResponseDTO;
import com.app.bdc_backend.model.dto.response.SelectVariationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductFacadeService productFacadeService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable("productId") String productId) {
        return ResponseEntity.ok(productFacadeService.getProduct(productId, false));
    }

    @PostMapping("/select_variation")
    @Operation(summary = "Select product variation")
    public ResponseEntity<SelectVariationResponseDTO> selectVariation(@RequestBody @Valid SelectVariationDTO selectVariationDTO) {
        return ResponseEntity.ok(productFacadeService.selectVariation(selectVariationDTO));
    }

}
