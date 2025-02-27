package com.app.bdc_backend.controller;

import com.app.bdc_backend.exception.DataNotExistException;
import com.app.bdc_backend.facade.ProductFacadeService;
import com.app.bdc_backend.model.dto.request.SelectVariationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductFacadeService productFacadeService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") String productId) {
        try{
            return ResponseEntity.ok(productFacadeService.getProduct(productId, false));
        }
        catch (DataNotExistException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/select_variation")
    public ResponseEntity<?> selectVariation(@RequestBody SelectVariationDTO selectVariationDTO) {
        try{
            return ResponseEntity.ok(productFacadeService.selectVariation(selectVariationDTO));
        }
        catch (DataNotExistException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
