package com.app.bdc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationDTO {

    private String fullName;

    private String username;

    private String password;

    private String phoneNumber;

    private Date dob;

}
