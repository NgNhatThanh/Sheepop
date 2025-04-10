package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.validation.StrongPassword;
import com.app.bdc_backend.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegistrationDTO {

    @NotBlank
    private String fullName;

    @NotBlank
    @Username
    private String username;

    @NotBlank
    @StrongPassword
    private String password;

    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    private String avatarUrl;

    private boolean fromSocial = false;

    @NotNull
    private Date dob;

}
