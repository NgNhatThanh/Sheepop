package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.validation.StrongPassword;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

    @NotNull
    private String oldPassword;

    @StrongPassword
    private String newPassword;

}
