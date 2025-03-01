package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CategoryDTO {

    private String id;

    private String name;

    private String description;

    private Date createdAt;

    private int productCount;

    private boolean hasChildren;

}
