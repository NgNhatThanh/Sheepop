package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerDTO {

    @NotNull(message = "Banner must have image")
    private String imageUrl;

    @NotNull(message = "Redirect Url cannot be empty")
    private String redirectUrl;

}
