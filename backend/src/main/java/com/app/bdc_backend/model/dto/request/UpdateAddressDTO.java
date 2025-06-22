package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressDTO {

    @NotBlank
    private String addressId;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String province;

    @NotBlank
    private String district;

    @NotBlank
    private String ward;

    @NotBlank
    private String detail;

    @NotNull
    private boolean primary;

}
