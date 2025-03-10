package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateProfileDTO {

    private String username;

    private String fullName;

    private Date dob;

    private String avatarUrl;

    private Gender gender;

}
