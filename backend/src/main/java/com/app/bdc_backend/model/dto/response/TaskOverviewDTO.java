package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskOverviewDTO {

    private int pendingOrders;

    private int preparingOrders;

    private int restrictedProducts;

}
