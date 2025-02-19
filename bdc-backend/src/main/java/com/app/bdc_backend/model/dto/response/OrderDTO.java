package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.order.Payment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDTO {

    private String id;

    private List<ShopOrderDTO> shopOrders;

    private Payment payment;

    private int status;

    private boolean isPending;

    private boolean isRated;

    private boolean isCompleted;

    private boolean isCancelable;

    private Date createdAt;

}
