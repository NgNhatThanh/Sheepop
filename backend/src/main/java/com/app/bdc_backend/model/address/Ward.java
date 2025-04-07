package com.app.bdc_backend.model.address;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wards")
@Getter
@Setter
public class Ward {

    @Id
    private String id;

    private int districtId;

    private String name;

}
