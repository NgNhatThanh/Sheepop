package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.ShopInfoFacadeService;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shopinfo")
@RequiredArgsConstructor
public class ShopInfoController {

    private final ShopInfoFacadeService shopInfoFacadeService;

    @GetMapping("/get_info")
    public ResponseEntity<?> getShopInfo(@RequestParam String username) {
        return ResponseEntity.ok(shopInfoFacadeService.getInfo(username));
    }

    @GetMapping("/get_product_list")
    public ResponseEntity<?> getProductList(@RequestParam String shopId,
                                            @RequestParam(required = false) String categoryId,
                                            @RequestParam String sortBy,
                                            @RequestParam String order,
                                            @RequestParam int page,
                                            @RequestParam int limit) {
        Page<ProductCardDTO> dtos = shopInfoFacadeService.getProductList(shopId, categoryId, sortBy, order, page, limit);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }

}
