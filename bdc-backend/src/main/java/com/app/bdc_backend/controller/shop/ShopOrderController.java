package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ShopOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/order")
public class ShopOrderController {

    private final ShopFacadeService shopFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getShopOrderList(@RequestParam(value = "type") int type,
                                              @RequestParam(value = "filterType", defaultValue = "0") int filterType,
                                              @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                              @RequestParam(value = "page") int page,
                                              @RequestParam (value = "limit") int limit){
        Page<ShopOrderDTO> shopOrderDTOS = shopFacadeService.getShopOrders(
                type,
                filterType,
                keyword,
                page,
                limit
        );
        PageResponse<ShopOrderDTO> response = new PageResponse<>(shopOrderDTOS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrderStatus(@RequestParam(value = "shopOrderId") String shopOrderId,
                                               @RequestParam(value = "currentStatus") int currentStatus) {
        shopFacadeService.updateOrder(shopOrderId, currentStatus);
        return ResponseEntity.ok().build();
    }

}
