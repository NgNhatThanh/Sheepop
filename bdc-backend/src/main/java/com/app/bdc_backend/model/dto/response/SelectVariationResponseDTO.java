package com.app.bdc_backend.model.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelectVariationResponseDTO {

    private int quantity;

    private long price = -1;

    private List<VariationDisplayIndicator> variationDisplayIndicators = new ArrayList<>();

}
