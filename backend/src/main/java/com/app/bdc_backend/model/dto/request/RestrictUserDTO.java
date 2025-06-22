package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestrictUserDTO {

    @NotBlank
    private String userId;

    @NotBlank
    private String reason;

}
