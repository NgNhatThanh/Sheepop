package com.app.bdc_backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BasicShippingOrderInfo {

    private int fee;

    private Date expectedDeliveryDate;

}
