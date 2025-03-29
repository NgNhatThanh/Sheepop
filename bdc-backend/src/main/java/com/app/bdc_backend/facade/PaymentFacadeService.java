package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.enums.PaymentGateway;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final VNPayService vnPayService;

    private final OrderService orderService;

    public String getPaymentUrl(String orderId, HttpServletRequest request){
        Order order = orderService.getOrderById(orderId);
        if(order == null)
            throw new RequestException("Order not found");
        return vnPayService.createPaymentUrl(order, request);
    }

    public boolean checkPayment(String gateway, Map<String, String> params){
        PaymentGateway paymentGateway = PaymentGateway.fromString(gateway);
        if (paymentGateway == PaymentGateway.VNPAY) {
            String txnRef = params.get("vnp_TxnRef");
            String[] p = txnRef.split("-");
            Order order = orderService.getOrderById(p[0]);
            if (order == null)
                throw new RequestException("Order not found");
            boolean ok = vnPayService.checkPayment(order, params);
            if(ok){
                Payment payment = order.getPayment();
                payment.setStatus(PaymentStatus.COMPLETED);
                orderService.savePayment(payment);
            }
            else return false;
        }
        return true;
    }
}
