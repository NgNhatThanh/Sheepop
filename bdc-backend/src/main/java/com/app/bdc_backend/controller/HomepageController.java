package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.HomepageFacadeService;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.OptionalLong;

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

}
