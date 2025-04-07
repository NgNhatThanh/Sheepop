package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.ShopOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ShopOrderTrack {

    private Date updatedAt;

    private int status;

}
