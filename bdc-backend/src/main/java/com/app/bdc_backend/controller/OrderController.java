package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.OrderFacadeService;
import com.app.bdc_backend.model.dto.request.OrderCancellationDTO;
import com.app.bdc_backend.model.dto.request.PlaceOrderDTO;
import com.app.bdc_backend.model.dto.response.PlaceOrderResponse;
import com.app.bdc_backend.model.order.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    @PostMapping("/place_order")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid PlaceOrderDTO dto,
                                        HttpServletRequest request) {
        return ResponseEntity.ok().body(orderFacadeService.placeOrder(dto, request));
    }

    @GetMapping("/get_order_list")
    public ResponseEntity<?> getOrderList(@RequestParam(value = "type") int type,
                                          @RequestParam(value = "limit") int limit,
                                          @RequestParam(value = "offset") int offset) {
        return ResponseEntity.ok(orderFacadeService.getOrderList(type, limit, offset));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam("shopOrderId") String shopOrderId) {
        return ResponseEntity.ok(orderFacadeService.getOrderDetail(shopOrderId));
    }

    @PostMapping("/mark_as_received")
    public ResponseEntity<?> markOrderAsReceived(@RequestParam(value = "shopOrderId") String shopOrderId){
        orderFacadeService.markOrderAsReceived(shopOrderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody @Valid OrderCancellationDTO dto) {
        orderFacadeService.cancelOrder(dto);
        return ResponseEntity.ok().build();
    }

}
