package com.app.bdc_backend.model.dto.request;

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
    private String username;

    @NotBlank
    private String password;

    private String phoneNumber;

    private String email;

    private String avatarUrl;

    private boolean fromSocial = false;

    @NotNull
    private Date dob;

}
