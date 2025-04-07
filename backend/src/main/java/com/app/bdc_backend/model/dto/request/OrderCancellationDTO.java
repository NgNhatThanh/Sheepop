package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderCancellationDTO {

    @NotBlank
    private String orderId;

    @NotEmpty
    private List<@NotBlank String> shopOrderIds = new ArrayList<>();

    private int whoCancel;

    @NotBlank
    private String cancelReason;

}
