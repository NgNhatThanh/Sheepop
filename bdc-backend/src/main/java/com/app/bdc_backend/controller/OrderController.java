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
        try{
            Order order = orderFacadeService.placeOrder(body);
            return ResponseEntity.ok().body(Map.of(
                    "order_id", order.getId().toString()
            ));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/get_order_list")
    public ResponseEntity<?> getOrderList(@RequestParam(value = "type") int type,
                                          @RequestParam(value = "limit") int limit,
                                          @RequestParam(value = "offset") int offset) {
        try{
            return ResponseEntity.ok(orderFacadeService.getOrderList(type, limit, offset));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam("shopOrderId") String shopOrderId) {
        try{
            return ResponseEntity.ok(orderFacadeService.getOrderDetail(shopOrderId));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/mark_as_received")
    public ResponseEntity<?> markOrderAsReceived(@RequestParam(value = "shopOrderId") String shopOrderId){
        try{
            orderFacadeService.markOrderAsReceived(shopOrderId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody OrderCancelationDTO dto) {
        try{
            orderFacadeService.cancelOrder(dto);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

}
