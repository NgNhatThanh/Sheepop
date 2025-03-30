package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateDTO {

    @NotBlank
    private String itemId;

    private boolean selected;

    @Min(1)
    private int quantity;

}
