package com.app.bdc_backend.model;

import com.app.bdc_backend.model.address.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentInfo {

    private Address from;

    private Address to;

    private int weight;

}
