package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.SaveProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ShopProductTableResponseDTO;
import com.app.bdc_backend.model.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/product")
public class ShopProductController {

    private final ShopFacadeService shopFacadeService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody SaveProductDTO productDTO) throws RuntimeException {
        shopFacadeService.addProduct(productDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody SaveProductDTO productDTO) throws RuntimeException {
        if(productDTO.getProductId() == null){
            throw new RuntimeException("Product ID is null");
        }
        shopFacadeService.updateProduct(productDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductForEdit(@PathVariable String productId) throws RuntimeException {
        Product product = shopFacadeService.getProductForEdit(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ShopProductTableResponseDTO>> getProductList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Page<ShopProductTableResponseDTO> responseDTOS = shopFacadeService.getProductList(page, limit);
        PageResponse<ShopProductTableResponseDTO> response = new PageResponse<>(responseDTOS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change_visible")
    public ResponseEntity<?> changeVisible(@RequestParam String productId) {
        shopFacadeService.changeProductVisible(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preview/{productId}")
    public ResponseEntity<?> preview(@PathVariable String productId){
        if(productId == null || productId.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid request: param"
            ));
        }
        return ResponseEntity.ok(shopFacadeService.previewProduct(productId));
    }

    @PostMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        if(productId == null || productId.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: param"
            ));
        }
        shopFacadeService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

}
