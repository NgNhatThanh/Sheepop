package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.OrderFacadeService;
import com.app.bdc_backend.model.dto.request.OrderCancellationDTO;
import com.app.bdc_backend.model.dto.request.PlaceOrderDTO;
import com.app.bdc_backend.model.dto.response.OffsetResponse;
import com.app.bdc_backend.model.dto.response.OrderDTO;
import com.app.bdc_backend.model.dto.response.PlaceOrderResponse;
import com.app.bdc_backend.model.dto.response.ShopOrderDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    @PostMapping("/place_order")
    @Operation(summary = "Place a new order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody @Valid PlaceOrderDTO dto,
                                        HttpServletRequest request) {
        return ResponseEntity.ok().body(orderFacadeService.placeOrder(dto, request));
    }

    @GetMapping("/get_order_list")
    @Operation(summary = "Get list of orders with filters")
    public ResponseEntity<OffsetResponse<OrderDTO>> getOrderList(@RequestParam(value = "type") int type,
                                          @RequestParam(value = "limit") int limit,
                                          @RequestParam(value = "offset") int offset) {
        return ResponseEntity.ok(orderFacadeService.getOrderList(type, limit, offset));
    }

    @GetMapping("/detail")
    @Operation(summary = "Get order detail by shop order ID")
    public ResponseEntity<ShopOrderDetailDTO> detail(@RequestParam("shopOrderId") String shopOrderId) {
        return ResponseEntity.ok(orderFacadeService.getOrderDetail(shopOrderId));
    }

    @PostMapping("/mark_as_received")
    @Operation(summary = "Mark order as received")
    public ResponseEntity<Void> markOrderAsReceived(@RequestParam(value = "shopOrderId") String shopOrderId){
        orderFacadeService.markOrderAsReceived(shopOrderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<Void> cancelOrder(@RequestBody @Valid OrderCancellationDTO dto) {
        orderFacadeService.cancelOrder(dto);
        return ResponseEntity.ok().build();
    }

}
