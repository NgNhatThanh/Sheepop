package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.OrderFacadeService;
import com.app.bdc_backend.model.dto.request.OrderCancelationDTO;
import com.app.bdc_backend.model.order.*;
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
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body) {
        Order order = orderFacadeService.placeOrder(body);
        return ResponseEntity.ok().body(Map.of(
                "order_id", order.getId().toString()
        ));
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
    public ResponseEntity<?> cancelOrder(@RequestBody OrderCancelationDTO dto) {
        orderFacadeService.cancelOrder(dto);
        return ResponseEntity.ok().build();
    }

}
