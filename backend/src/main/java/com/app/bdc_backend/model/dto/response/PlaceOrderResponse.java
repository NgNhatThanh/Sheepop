package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderResponse {

    private String orderId;

    private String paymentUrl;

}
