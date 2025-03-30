package com.app.bdc_backend.model.dto;

import com.app.bdc_backend.model.order.ShopOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopOrderPageImpl {

    private int totalElements;

    private List<ShopOrder> content;

}
