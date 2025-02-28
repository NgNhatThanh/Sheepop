package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.request.CreateReviewDTO;
import com.app.bdc_backend.model.dto.response.ProductReviewDTO;
import com.app.bdc_backend.model.dto.response.ReviewListDTO;
import com.app.bdc_backend.model.enums.ReviewFilter;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductReview;
import com.app.bdc_backend.model.product.ProductReviewMedia;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.ReviewService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewFacadeService {

    private final ReviewService reviewService;

    private final OrderService orderService;

    private final ProductService productService;

    public void createReview(CreateReviewDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShopOrder shopOrder = orderService.getShopOrderById(dto.getShopOrderId());
        if(shopOrder == null){
            throw new RequestException("Invalid request: shop order id");
        }
        if(shopOrder.getStatus() != ShopOrderStatus.COMPLETED){
            throw new RequestException("Invalid request: order status");
        }
        if(shopOrder.getItems().size() != dto.getItemReviews().size()){
            throw new RequestException("Invalid request: items amount");
        }
        if(!shopOrder.getUser().getUsername().equals(username)){
            throw new RequestException("Invalid request: reviewer");
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
            throw new RequestException("Invalid request: invalid item(s)");
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
    }

    public ReviewListDTO getProductReviewList(String productId,
                                                       int rating,
                                                       int filterType,
                                                       int page,
                                                       int limit) {
        Product product = productService.findById(productId);
        if(product == null){
            throw new RequestException("Invalid request: Product not found");
        }
        if(rating < 0 || rating > 5 || filterType < 0 || filterType > 2){
            throw new RequestException("Invalid request: rating || filterType");
        }
        List<ProductReview> reviewList;
        if(filterType == ReviewFilter.ALL){
            if(rating == 0) reviewList = reviewService.getAllReviewOfProduct(product.getId(), page, limit);
            else reviewList = reviewService.getAllReviewOfProductByRating(product.getId(), rating, page, limit);
        }
        else if(filterType == ReviewFilter.WITH_CONTENT){
            reviewList = reviewService.getAllReviewOfProductHasContent(product.getId(), page, limit);
        }
        else reviewList = reviewService.getAllReviewOfProductHasMedia(product.getId(), page, limit);
        ReviewListDTO dto = new ReviewListDTO();
        List<ProductReviewDTO> reviewDTOList = reviewList.stream().map(this::toProductReviewDTO).toList();
        dto.setReviews(reviewDTOList);
        dto.setSummary(reviewService.getProductReviewSummary(product.getId()));
        return dto;
    }

    private ProductReviewDTO toProductReviewDTO(ProductReview productReview){
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(productReview.getId().toString());
        dto.setRating(productReview.getRating());
        dto.setContent(productReview.getContent());
        dto.setMediaList(productReview.getMediaList());
        dto.setReactionCount(productReview.getReactionCount());
        dto.setCreatedAt(productReview.getCreatedAt());

        dto.getReviewer().setUsername(productReview.getReviewer().getUsername());
        dto.getReviewer().setAvatarUrl(productReview.getReviewer().getAvatarUrl());

        dto.getItem().setAttributes(productReview.getOrderItem().getAttributes());
        dto.getItem().setQuantity(productReview.getOrderItem().getQuantity());
        return dto;
    }

}
