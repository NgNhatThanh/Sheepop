package com.app.bdc_backend.model.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VariationDisplayIndicator{

    private String name;

    private List<VariationOption> variationOptions = new ArrayList<>();

    @Getter
    @Setter
    @EqualsAndHashCode(exclude = "available")
    public static class VariationOption{

        private String value;

        private boolean available = true;

    }

}
