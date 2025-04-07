package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.model.order.ShopOrderTrack;
import com.app.bdc_backend.model.user.UserAddress;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ShopOrderDetailDTO {

    private String id;

    private String shopUsername;

    private String shopName;

    private int status;

    private List<ShopOrderTrack> tracks;

    private UserAddress address;

    private Payment payment;

    private List<OrderItemDTO> items;

    private String cancelReason;

    private int canceledBy;

    private int shippingFee;

}
