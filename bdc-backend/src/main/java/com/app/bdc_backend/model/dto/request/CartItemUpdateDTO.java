package com.app.bdc_backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateDTO {

    private String itemId;

    private boolean selected;

    private int quantity;

}
