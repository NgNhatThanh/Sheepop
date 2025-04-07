package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank
    private String token;

    @StrongPassword
    private String newPassword;

}
