package com.app.bdc_backend.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceOrderDTO {

    @NotBlank
    private String addressId;

    @NotBlank
    private String paymentType;

    @NotEmpty
    private List<@Valid PlaceShopOrderDTO> shopOrders;

    @Getter
    @Setter
    public static class PlaceShopOrderDTO{

        @NotBlank
        private String shopId;

        private int shippingFee;

        private int totalPrice;

    }

}
