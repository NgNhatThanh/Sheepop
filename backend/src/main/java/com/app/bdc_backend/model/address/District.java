package com.app.bdc_backend.model.address;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.DefaultCall;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "districts")
@Getter
@Setter
public class District {

    @Id
    private int id;

    private int provinceId;

    private String name;

}
