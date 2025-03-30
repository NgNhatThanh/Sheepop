package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.facade.admin.AdminOrderFacadeService;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ShopOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/order")
public class AdminOrderController {

    private final AdminOrderFacadeService adminOrderFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getOrderList(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "filterType", defaultValue = "0") int filterType,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam int sortType,
            @RequestParam(value = "page") int page,
            @RequestParam (value = "limit") int limit){
        Page<ShopOrderDTO> shopOrderDTOS = adminOrderFacadeService.getOrderList(
                type,
                filterType,
                keyword,
                sortType,
                page,
                limit
        );
        PageResponse<ShopOrderDTO> response = new PageResponse<>(shopOrderDTOS);
        return ResponseEntity.ok(response);
    }

}
