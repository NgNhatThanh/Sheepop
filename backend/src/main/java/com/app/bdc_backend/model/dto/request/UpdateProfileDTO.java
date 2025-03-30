package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateProfileDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String fullName;

    @NotBlank
    private Date dob;

    @NotBlank
    private String avatarUrl;

    @NotNull(message = "Gender cannot be empty")
    private Gender gender;

}
