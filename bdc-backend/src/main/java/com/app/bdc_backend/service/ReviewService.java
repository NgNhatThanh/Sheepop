package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.ProductReviewMediaRepository;
import com.app.bdc_backend.dao.ReviewRepository;
import com.app.bdc_backend.model.product.ProductMedia;
import com.app.bdc_backend.model.product.ProductReview;
import com.app.bdc_backend.model.product.ProductReviewMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductReviewMediaRepository productReviewMediaRepository;

    private final MongoTemplate mongoTemplate;

    public void saveAllReview(List<ProductReview> reviews){
        reviewRepository.saveAll(reviews);
    }

    public void saveAllMedia(List<ProductReviewMedia> mediaList){
        productReviewMediaRepository.saveAll(mediaList);
    }

//    public Map<String, String> getCountAndAverageReviewOfShop(String shopId){
//        int count = reviewRepository.countByShopId(shopId);
//        float avg = averageRatingByShopId(shopId);
//        return Map.of(
//                "reviewCount", String.valueOf(count),
//                "averageRating", String.valueOf(avg)
//        );
//    }
//
//    public float averageRatingByShopId(String shopId){
//        MatchOperation operation = Aggregation.match(Criteria.where("shopId").is(shopId));
//        Aggregation aggregation = Aggregation.newAggregation(
//                operation,
//                group("shopId")
//                        .avg("rating").as("averageRating")
//        );
//        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "product_reviews", Map.class);
//        return Float.parseFloat(results.getUniqueMappedResult().get("averageRating").toString());
//    }

}
