package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDisplayCategoryDTO {

    @NotNull(message = "img url cannot be empty")
    @NotEmpty(message = "img url cannot be empty")
    private String thumbnailUrl;

}
