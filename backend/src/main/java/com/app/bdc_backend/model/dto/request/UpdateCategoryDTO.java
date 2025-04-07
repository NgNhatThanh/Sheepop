package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryDTO {

    @NotBlank(message = "Category name cannot be empty")
    private String name;

    @NotBlank(message = "Category description cannot be empty")
    private String description;

}
