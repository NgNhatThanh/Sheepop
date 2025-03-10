package com.app.bdc_backend.model.address;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "provinces")
@Getter
@Setter
public class Province {

    @Id
    private int id;

    private String name;

}
