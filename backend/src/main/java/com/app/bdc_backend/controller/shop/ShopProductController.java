package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.SaveProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductResponseDTO;
import com.app.bdc_backend.model.dto.response.ShopProductTableResponseDTO;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/product")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class ShopProductController {

    private final ShopFacadeService shopFacadeService;

    @PostMapping("/add")
    @Operation(summary = "Add new product")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid SaveProductDTO productDTO) {
        shopFacadeService.addProduct(productDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    @Operation(summary = "Update product information")
    public ResponseEntity<Void> updateProduct(@RequestBody @Valid SaveProductDTO productDTO) {
        if(productDTO.getProductId() == null){
            throw new RuntimeException("Product ID is null");
        }
        shopFacadeService.updateProduct(productDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_categories")
    @Operation(summary = "Get shop categories list")
    public ResponseEntity<List<Category>> getShopCategories() {
        return ResponseEntity.ok().body(shopFacadeService.getShopCategories());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product information for editing")
    public ResponseEntity<Product> getProductForEdit(@PathVariable String productId) {
        Product product = shopFacadeService.getProductForEdit(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/list")
    @Operation(summary = "Get product list with filters and sorting")
    public ResponseEntity<PageResponse<ShopProductTableResponseDTO>> getProductList(
            @RequestParam int type,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String categoryId,
            @RequestParam int sortType,
            @RequestParam int page,
            @RequestParam int limit) {
        Page<ShopProductTableResponseDTO> responseDTOS = shopFacadeService.getProductList(
                type, keyword, categoryId, sortType, page, limit);
        PageResponse<ShopProductTableResponseDTO> response = new PageResponse<>(responseDTOS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change_visible")
    @Operation(summary = "Change product visibility status")
    public ResponseEntity<Void> changeVisible(@RequestParam String productId) {
        shopFacadeService.changeProductVisible(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preview/{productId}")
    @Operation(summary = "Preview product information")
    public ResponseEntity<ProductResponseDTO> preview(@PathVariable String productId){
        if(productId == null || productId.isEmpty()){
            throw new RuntimeException("Invalid request: param");
        }
        return ResponseEntity.ok(shopFacadeService.previewProduct(productId));
    }

    @PostMapping("/delete/{productId}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        if(productId == null || productId.isEmpty()){
            throw new RuntimeException("Invalid request: param");
        }
        shopFacadeService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
}
