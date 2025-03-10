package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.order.PaymentRepository;
import com.app.bdc_backend.model.order.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

}
