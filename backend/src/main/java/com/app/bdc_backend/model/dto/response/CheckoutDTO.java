package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckoutDTO {

    private List<ShopCheckoutDTO> shopCheckouts;

}
