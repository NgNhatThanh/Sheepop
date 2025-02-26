package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.ReviewFacadeService;
import com.app.bdc_backend.model.dto.request.CreateReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewFacadeService reviewFacadeService;

    @PostMapping("/create_review")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewDTO dto){
        try{
            reviewFacadeService.createReview(dto);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/get_review_list")
    public ResponseEntity<?> getReviewList(@RequestParam("productId") String productId,
                                           @RequestParam("rating") int rating,
                                           @RequestParam("filterType") int filterType,
                                           @RequestParam("page") int page,
                                           @RequestParam("limit") int limit){
        try{
            return ResponseEntity.ok(reviewFacadeService.getProductReviewList(
                    productId,
                    rating,
                    filterType,
                    page,
                    limit
            ));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }



}
