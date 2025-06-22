package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.PaymentFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class PaymentController {

    private final PaymentFacadeService paymentFacadeService;

    @GetMapping("/payment_url")
    @Operation(summary = "Get payment URL")
    public ResponseEntity<Map<String, String>> getPaymentUrl(@RequestParam(value = "orderId") String orderId,
                                                           HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("url", paymentFacadeService.getPaymentUrl(orderId, request)));
    }

    @PostMapping("/check")
    @Operation(summary = "Check payment status")
    public ResponseEntity<Map<String, String>> checkPayment(@RequestParam(value = "gateway") String gateway,
                                                          @RequestParam Map<String, String> params) {
        boolean ok = paymentFacadeService.checkPayment(gateway, params);
        return ResponseEntity.ok(Map.of("status", ok ? "success" : "failed"));
    }
}
