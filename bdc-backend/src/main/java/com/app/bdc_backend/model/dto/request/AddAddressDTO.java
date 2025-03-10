package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAddressDTO {

    private String receiverName;

    private String senderName;

    private String phoneNumber;

    private String province;

    private String district;

    private String ward;

    private String detail;

    private boolean primary;

}
