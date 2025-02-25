package com.app.bdc_backend.dao.order;

import com.app.bdc_backend.model.dto.BasicReviewInfo;
import com.app.bdc_backend.model.dto.response.ReviewSummary;
import com.app.bdc_backend.model.product.ProductReview;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<ProductReview, String> {

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item'} }",
        "{ $lookup: { from: 'products', localField: 'order_item.product', foreignField: '_id', as: 'product' } }",
        "{ $match: { 'product._id': ?0 } }",
        "{ $group: { _id: '$product._id', 'averageRating': { $avg: '$rating' }, 'totalReviews': { $sum: 1 } } }"
    })
    BasicReviewInfo findAverageRatingAndTotalReviewsByProductId(ObjectId productId);

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' } }",
        "{ $lookup: { from: 'products', localField: 'order_item.product', foreignField: '_id', as: 'product' } }",
        "{ $lookup: { from: 'shops', localField: 'product.shop', foreignField: '_id', as: 'shop' } }",
        "{ $match: { 'shop._id': ?0 } }",
        "{ $group: { _id: '$shop._id', averageRating: { $avg: '$rating' }, totalReviews: { $sum: 1 } }}"
    })
    BasicReviewInfo findBasicReviewInfoByShopId(ObjectId shopId);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' }}",
            "{ $match: { 'order_item.product': ?0 }}",
            "{ $group: { _id: '$order_item.product', " +
                    "countRating: { $push: '$rating' }, " +
                    "countWithContent: { $sum: { $cond: [ { $ne: ['$content', ''] }, 1, 0 ] }}," +
                    "countWithMedia: { $sum: { $cond: [{ $ne: [{ $size: '$mediaList' }, 0]}, 1, 0] }}}}",
            "{ $project: { _id: 1, " +
                    "countRatings: [" +
                        "{ $size: { $filter: { input: '$countRating', as: 'r', cond: { $eq: ['$$r', 1] }}}}," +
                        "{ $size: { $filter: { input: '$countRating', as: 'r', cond: { $eq: ['$$r', 2] }}}}, " +
                        "{ $size: { $filter: { input: '$countRating', as: 'r', cond: { $eq: ['$$r', 3] }}}}, " +
                        "{ $size: { $filter: { input: '$countRating', as: 'r', cond: { $eq: ['$$r', 4] }}}}, " +
                        "{ $size: { $filter: { input: '$countRating', as: 'r', cond: { $eq: ['$$r', 5] }}}}], " +
                    "countWithContent: 1," +
                    "countWithMedia: 1}}"
    })
    ReviewSummary findReviewSummaryByProductId(ObjectId productId);

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' }}",
        "{ $match: { 'order_item.product': ?0 }}",
        "{ $sort: { 'createdAt': -1 } }",
        "{ $skip: ?1 }",
        "{ $limit: ?2 }"
    })
    List<ProductReview> findAllByProductId(ObjectId productId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' }}",
            "{ $match: { 'order_item.product': ?0, 'rating': ?1 }}",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    List<ProductReview> findAllByProductIdAndRating(ObjectId productId, int rating, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' }}",
            "{ $match: { 'order_item.product': ?0, 'content': { $ne: \"\" } }}",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<ProductReview> findAllByProductIdAndContentNotEmpty(ObjectId productId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'order_items', localField: 'orderItem', foreignField: '_id', as: 'order_item' }}",
            "{ $match: { 'order_item.product': ?0, 'mediaList': { $exists: true, $ne: [] } }}",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<ProductReview> findAllByProductIdAndMediaNotEmpty(ObjectId productId, int skip, int limit);

}
