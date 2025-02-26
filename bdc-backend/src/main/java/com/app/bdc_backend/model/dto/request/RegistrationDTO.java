package com.app.bdc_backend.model.dto.request;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegistrationDTO {

    private String fullName;

    private String username;

    private String password;

    private String phoneNumber;

    private String email;

    private String avatarUrl;

    private boolean fromSocial = false;

    private Date dob;

}
