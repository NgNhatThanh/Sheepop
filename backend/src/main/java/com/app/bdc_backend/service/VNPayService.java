package com.app.bdc_backend.service;

import com.app.bdc_backend.config.VNPayConfig;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;

    @Value("${payment.vnPay.max_time}")
    private int maxPaymentTime; // in second

    private final String SUCCESS_CODE = "00";

    public String createPaymentUrl(Order order,
                                   HttpServletRequest request){
        Payment payment = order.getPayment();
        if(payment.getType() != PaymentType.BANK_TRANSFER)
            throw new RequestException("Payment type is not BANK_TRANSFER");
        if(payment.getStatus() == PaymentStatus.EXPIRED)
            throw new RequestException("Payment expired");
        if(payment.getStatus() != PaymentStatus.PENDING)
            throw new RequestException("Payment status is not PENDING");
        Date expiredDate = payment.getExpireAt();
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        Date currentTime = new Date();
        if(currentTime.after(expiredDate)){
            payment.setStatus(PaymentStatus.EXPIRED);
            order.setPayment(payment);
            throw new RequestException("Payment expired");
        }
        Map<String, String> params = vnPayConfig.getConfig();
        long diffInMillis = expiredDate.getTime() - currentTime.getTime();
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
        int allowedTime = Math.min(remainingSeconds, maxPaymentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(now.getTime());
        params.put("vnp_CreateDate", vnp_CreateDate);
        now.add(Calendar.SECOND, allowedTime);
        String vnp_ExpireDate = formatter.format(now.getTime());
        params.put("vnp_ExpireDate", vnp_ExpireDate);
        params.put("vnp_Amount", String.valueOf(payment.getAmount() * 100L));
        String ref = order.getId() + "-" + System.currentTimeMillis();
        params.put("vnp_TxnRef", ref);
        params.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getId());
        String ipAddr = VNPayUtil.getIpAddress(request);
        params.put("vnp_IpAddr", ipAddr);
        String queryString = VNPayUtil.createPaymentUrl(params, true);
        String hashData = VNPayUtil.createPaymentUrl(params, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryString += "&vnp_SecureHash=" + vnpSecureHash;
        return vnPayConfig.getVnp_PayUrl() + "?" + queryString;
    }

    public boolean checkPayment(Order order, Map<String, String> params) {
        String code = params.get("vnp_ResponseCode");
        if(code.equals(SUCCESS_CODE)){
            long amount = Long.parseLong(params.get("vnp_Amount")) / 100L;
            Payment payment = order.getPayment();
            return amount == payment.getAmount();
        }
        else return false;
    }
}
