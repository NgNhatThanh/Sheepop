package com.app.bdc_backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCategoryDTO {

    private String parentId;

    private String name;

    private String description;

}
