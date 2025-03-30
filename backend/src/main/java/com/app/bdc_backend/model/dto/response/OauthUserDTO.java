package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthUserDTO {

    private String username;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

}
