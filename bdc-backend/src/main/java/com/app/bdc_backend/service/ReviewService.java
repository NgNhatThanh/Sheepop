package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.product.ProductReviewMediaRepository;
import com.app.bdc_backend.dao.order.ReviewRepository;
import com.app.bdc_backend.model.dto.BasicReviewInfo;
import com.app.bdc_backend.model.dto.response.ReviewSummary;
import com.app.bdc_backend.model.product.ProductReview;
import com.app.bdc_backend.model.product.ProductReviewMedia;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductReviewMediaRepository productReviewMediaRepository;

    public void saveAllReview(List<ProductReview> reviews){
        reviewRepository.saveAll(reviews);
    }

    public void saveAllMedia(List<ProductReviewMedia> mediaList){
        productReviewMediaRepository.saveAll(mediaList);
    }

    public BasicReviewInfo getProductReviewInfo(ObjectId productId){
        return reviewRepository.findAverageRatingAndTotalReviewsByProductId(productId);
    }

    public BasicReviewInfo getShopReviewInfo(ObjectId shopId){
        return reviewRepository.findBasicReviewInfoByShopId(shopId);
    }

    public ReviewSummary getProductReviewSummary(ObjectId productId){
        return reviewRepository.findReviewSummaryByProductId(productId);
    }

    public List<ProductReview> getAllReviewOfProduct(ObjectId productId, int page, int limit){
        int skip = page * limit;
        return reviewRepository.findAllByProductId(productId, skip, limit);
    }

    public List<ProductReview> getAllReviewOfProductByRating(ObjectId productId, int rating, int page, int limit){
        int skip = page * limit;
        return reviewRepository.findAllByProductIdAndRating(productId, rating, skip, limit);
    }

    public List<ProductReview> getAllReviewOfProductHasContent(ObjectId productId, int page, int limit){
        int skip = page * limit;
        return reviewRepository.findAllByProductIdAndContentNotEmpty(productId, skip, limit);
    }

    public List<ProductReview> getAllReviewOfProductHasMedia(ObjectId productId, int page, int limit){
        int skip = page * limit;
        return reviewRepository.findAllByProductIdAndMediaNotEmpty(productId, skip, limit);
    }

}
