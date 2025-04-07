package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAddressDTO {

    private String receiverName;

    private String senderName;

    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @NotBlank
    private String province;

    @NotBlank
    private String district;

    @NotBlank
    private String ward;

    @NotBlank(message = "Address detail cannot be empty")
    private String detail;

    private boolean primary;

}
