package com.app.bdc_backend.controller;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.HomepageFacadeService;
import com.app.bdc_backend.model.dto.request.ProductSearchFilters;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/homepage")
public class HomepageController {

    private final HomepageFacadeService homepageFacadeService;

    @GetMapping("/get-items")
    public ResponseEntity<?> getItems(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "limit", defaultValue = "50") int limit){
        Page<ProductCardDTO> cardDTOS = homepageFacadeService.getHomePageItems(page, limit);
        return ResponseEntity.ok(new PageResponse<>(cardDTOS));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItems(@RequestParam String keyword,
                                         @RequestParam(required = false, defaultValue = "relevance") String sortBy,
                                         @RequestParam(required = false, defaultValue = "desc") String order,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "60") int limit,
                                         @RequestParam(required = false) List<String> categoryIds,
                                         @RequestParam(required = false) List<String> locations,
                                         @RequestParam(required = false) Integer minPrice,
                                         @RequestParam(required = false) Integer maxPrice,
                                         @RequestParam(required = false) Integer minRating){
        log.info("Locations: {}", locations);
        if(minPrice != null && maxPrice != null && minPrice > maxPrice)
            throw new RequestException("Invalid request: price range is invalid");
        if(minRating != null && minRating > 5)
            throw new RequestException("Invalid request: min rating is invalid");
        if(categoryIds != null && categoryIds.isEmpty())
            throw new RequestException("Invalid request: categoryId is invalid");
        if(locations != null && locations.isEmpty())
            throw new RequestException("Invalid request: location is invalid");
        ProductSearchFilters filters = new ProductSearchFilters(
                categoryIds == null ? new ArrayList<>() : categoryIds,
                locations == null ? new ArrayList<>() : locations,
                minPrice, maxPrice, minRating);
        Page<ProductCardDTO> dtos = homepageFacadeService.searchProducts(keyword, sortBy, order, page, limit,
                filters);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }

    @GetMapping("/get_categories_filter")
    public ResponseEntity<?> getCategoriesFilter(){
        return ResponseEntity.ok(homepageFacadeService.getCategoriesFilter());
    }

    @GetMapping("/get_locations_filter")
    public ResponseEntity<?> getLocationsFilter(){
        return ResponseEntity.ok(homepageFacadeService.getLocationsFilter());
    }

}
