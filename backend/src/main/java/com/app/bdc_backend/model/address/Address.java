package com.app.bdc_backend.model.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Address {

    protected Province province;

    protected District district;

    protected Ward ward;

    protected String detail;

}
