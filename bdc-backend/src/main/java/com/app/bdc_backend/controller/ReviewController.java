package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.dto.request.CreateReviewDTO;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.ProductReview;
import com.app.bdc_backend.model.product.ProductReviewMedia;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ReviewService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    private final OrderService orderService;

    @PostMapping("/create_review")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewDTO dto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShopOrder shopOrder = orderService.getShopOrderById(dto.getShopOrderId());
        if(shopOrder == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: shop order id"
            ));
        }
        if(shopOrder.getStatus() != ShopOrderStatus.COMPLETED){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: order status"
            ));
        }
        if(shopOrder.getItems().size() != dto.getItemReviews().size()){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: items amount"
            ));
        }
        if(!shopOrder.getUser().getUsername().equals(username)){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: reviewer"
            ));
        }
        boolean ok = true;
        for(OrderItem item : shopOrder.getItems()){
            boolean tmp = false;
            for(CreateReviewDTO.ItemReview itemReview : dto.getItemReviews()){
                if(itemReview.getOrderItemId().equals(item.getId().toString())){
                    tmp = true;
                    break;
                }
            }
            if(!tmp){
                ok = false;
                break;
            }
        }
        if(!ok){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: invalid item(s)"
            ));
        }
        List<ProductReview> reviews = new ArrayList<>();
        List<ProductReviewMedia> medias = new ArrayList<>();
        for(OrderItem item : shopOrder.getItems()){
            for(CreateReviewDTO.ItemReview itemReview : dto.getItemReviews()){
                if(itemReview.getOrderItemId().equals(item.getId().toString())){
                    ProductReview review = ModelMapper.getInstance().map(itemReview, ProductReview.class);
                    review.setReviewer(shopOrder.getUser());
                    review.setOrderItem(item);
                    review.setCreatedAt(new Date());
                    review.getMediaList().stream().map(medias::add);
                    reviews.add(review);
                    break;
                }
            }
        }
        reviewService.saveAllMedia(medias);
        reviewService.saveAllReview(reviews);
        shopOrder.setStatus(ShopOrderStatus.RATED);
        orderService.saveAllShopOrders(List.of(shopOrder));
        return ResponseEntity.ok().build();
    }

}
