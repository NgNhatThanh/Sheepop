package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.admin.AdminProductFacadeService;
import com.app.bdc_backend.model.dto.request.RestrictProductDTO;
import com.app.bdc_backend.model.dto.response.AdminProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.product.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/product")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminProductController {

    private final AdminProductFacadeService adminProductFacadeService;

    @GetMapping("/get_list")
    @Operation(
            summary = "Get list of all products"
    )
    public ResponseEntity<PageResponse<AdminProductDTO>> getProductList(@RequestParam int type,
                                                                @RequestParam String productName,
                                                                @RequestParam String shopName,
                                                                @RequestParam int page,
                                                                @RequestParam int limit){
        return ResponseEntity.ok(new PageResponse<>(
                adminProductFacadeService.getProductList(type, productName, shopName, page, limit)
        ));
    }

    @GetMapping("/detail/{productId}")
    @Operation(
            summary = "Get detail information of a product by Id"
    )
    public ResponseEntity<Product> getProductDetail(@PathVariable String productId){
        if(productId == null || productId.isEmpty())
            throw new RequestException("Invalid request: empty params");
        return ResponseEntity.ok(adminProductFacadeService.getDetailProduct(productId));
    }

    @PostMapping("/restrict")
    @Operation(
            summary = "Restrict a product"
    )
    public ResponseEntity<?> restrictProduct(@RequestBody @Valid RestrictProductDTO dto){ // include productId and reason
        adminProductFacadeService.restrictProduct(dto.getProductId(), dto.getReason());
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/open_restrict/{productId}")
    @Operation(
            summary = "Remove restriction of a product"
    )
    public ResponseEntity<?> openRestrict(@PathVariable String productId){
        if(productId == null || productId.isEmpty()){
           throw new RequestException("Invalid request: empty params");
        }
        adminProductFacadeService.openRestrict(productId);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

}
