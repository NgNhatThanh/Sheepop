package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.ReviewFacadeService;
import com.app.bdc_backend.model.dto.request.CreateReviewDTO;
import com.app.bdc_backend.model.dto.response.ReviewListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewFacadeService reviewFacadeService;

    @PostMapping("/create_review")
    @Operation(summary = "Create new review")
    @SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
    public ResponseEntity<Void> createReview(@RequestBody @Valid CreateReviewDTO dto){
        reviewFacadeService.createReview(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_review_list")
    @Operation(summary = "Get product reviews list with filters")
    public ResponseEntity<ReviewListDTO> getReviewList(@RequestParam("productId") String productId,
                                                      @RequestParam("rating") int rating,
                                                      @RequestParam("filterType") int filterType,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("limit") int limit){
        return ResponseEntity.ok(reviewFacadeService.getProductReviewList(
                productId,
                rating,
                filterType,
                page,
                limit
        ));
    }
}
