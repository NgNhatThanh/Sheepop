package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.admin.AdminProductFacadeService;
import com.app.bdc_backend.model.dto.request.RestrictProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/product")
public class AdminProductController {

    private final AdminProductFacadeService adminProductFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getProductList(@RequestParam int type,
                                            @RequestParam String productName,
                                            @RequestParam String shopName,
                                            @RequestParam int page,
                                            @RequestParam int limit){
        return ResponseEntity.ok(new PageResponse<>(
                adminProductFacadeService.getProductList(type, productName, shopName, page, limit)
        ));
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId){
        if(productId == null || productId.isEmpty())
            throw new RequestException("Invalid request: empty params");
        return ResponseEntity.ok(adminProductFacadeService.getDetailProduct(productId));
    }

    @PostMapping("/restrict")
    public ResponseEntity<?> restrictProduct(@RequestBody @Valid RestrictProductDTO dto){ // include productId and reason
        adminProductFacadeService.restrictProduct(dto.getProductId(), dto.getReason());
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/open_restrict/{productId}")
    public ResponseEntity<?> openRestrict(@PathVariable String productId){
        if(productId == null || productId.isEmpty()){
           throw new RequestException("Invalid request: empty params");
        }
        adminProductFacadeService.openRestrict(productId);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

}
