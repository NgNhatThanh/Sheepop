package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.PaymentFacadeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentFacadeService paymentFacadeService;

    @GetMapping("/payment_url")
    public ResponseEntity<?> getPaymentUrl(@RequestParam String orderId,
                                           HttpServletRequest request) {
        return ResponseEntity.ok().body(Map.of(
                "url", paymentFacadeService.getPaymentUrl(orderId, request)
        ));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkPayment(@RequestParam String gateway,
                                          @RequestParam Map<String, String> params) {
        boolean ok = paymentFacadeService.checkPayment(gateway, params);
        return ResponseEntity.ok().body(Map.of(
                "status", ok ? "success" : "failed"
        ));
    }

}
