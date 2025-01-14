package com.app.bdc_backend.model.dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private String id;

    private String username;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

    private Date dob;

    private Date createdAt;

}
