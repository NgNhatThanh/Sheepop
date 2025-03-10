package com.app.bdc_backend.model.dto;

import com.app.bdc_backend.model.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopPageImpl {

    private int totalElements;

    private List<Shop> content;

}
