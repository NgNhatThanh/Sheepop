package com.app.bdc_backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductSearchFilters {

    private List<String> categoryIds;
    
    private String location;
    
    private Integer minPrice;
    
    private Integer maxPrice;
    
    private Integer minRating;

}
