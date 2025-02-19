package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ShopOrderDTO {

    private String id;

    private String username;

    private String name;

    private boolean completedPayment;

    private int shippingFee;

    private int status;

    private Date createdAt;

    private List<OrderItemDTO> items;

}
