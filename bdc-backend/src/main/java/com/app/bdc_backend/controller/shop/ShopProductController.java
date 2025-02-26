package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.AddProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ShopProductTableResponseDTO;
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
    public ResponseEntity<?> addProduct(@RequestBody AddProductDTO productDTO) {
        try{
            shopFacadeService.addProduct(productDTO);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ShopProductTableResponseDTO>> getProductList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Page<ShopProductTableResponseDTO> responseDTOS = shopFacadeService.getProductList(page, limit);
        PageResponse<ShopProductTableResponseDTO> response = new PageResponse<>(responseDTOS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change_visible")
    public ResponseEntity<?> changeVisible(@RequestParam String productId){
        try{
            shopFacadeService.changeProductVisible(productId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }



}
