package com.app.bdc_backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderCancelationDTO {

    private String orderId;

    private List<String> shopOrderIds = new ArrayList<>();

    private int whoCancel;

    private String cancelReason;

}
