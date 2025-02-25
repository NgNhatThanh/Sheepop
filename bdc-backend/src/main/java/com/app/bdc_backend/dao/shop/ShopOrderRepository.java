package com.app.bdc_backend.dao.shop;

import com.app.bdc_backend.model.dto.ShopOrderPageImpl;
import com.app.bdc_backend.model.dto.response.MyPageImpl;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ShopOrderRepository extends MongoRepository<ShopOrder, String> {

    List<ShopOrder> findByOrder(Order order);

    Page<ShopOrder> findLastByShopOrderByCreatedAtDesc(Shop shop, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'users', localField: 'user', foreignField: '_id', as: 'buyer' } }",
            "{ $unwind: '$buyer' }",
            "{ $match: { 'buyer._id': ?0, 'status': { $in: ?1 } } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    List<ShopOrder> findLastByUserAndStatusInOrderByCreatedAtDesc(ObjectId userId,
                                                                  List<Integer> status,
                                                                  int offset,
                                                                  int limit);

    Page<ShopOrder> findLastByShopAndStatusInOrderByCreatedAtDesc(Shop shop,
                                                                  List<Integer> status,
                                                                  Pageable pageable);

    @Aggregation(pipeline = {
        "{ $match: { shop: ?0, status: { $gte: 5 } } }",
        "{ $group: { _id: null, count: { $sum: { $size: '$items' } } } }"
    })
    int countShopSold(ObjectId shopId);

    Page<ShopOrder> findLastByShopAndIdAndStatusInOrderByCreatedAtDesc(
            Shop shop,
            String id,
            List<Integer> status,
            Pageable pageable);

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'users', localField: 'user', foreignField: '_id', as: 'buyer' }}",
        "{ $match: { shop: ?0, 'buyer.fullName': { $regex: ?1, $options: 'i'}, status: { $in: ?2 }}}",
        "{ $sort: { createdAt: -1 } }",
        "{ $group: { _id: null, totalElements: { $sum: 1 }, content: { $push: '$$ROOT' }}}",
        "{ $project: { _id: 0, totalElements: 1, content: { $slice: ['$content', ?3, ?4] } }}"
    })
    ShopOrderPageImpl findByShopAndUserFullNameContainingIgnoreCaseAndStatusInOrderByCreatedAtDesc(
            ObjectId shopId,
            String keyword,
            List<Integer> status,
            long offset,
            int limit
    );

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'order_items', localField: 'items', foreignField: '_id', as: 'order_items' }}",
        "{ $lookup: { from: 'products', localField: 'order_items.product', foreignField: '_id', as: 'products' }}",
        "{ $match: { shop: ?0, 'products.name': { $regex: ?1, $options: 'i' }, status: { $in: ?2 } }}",
        "{ $sort: { createdAt: -1 } }",
        "{ $group: { _id: null, totalElements: { $sum: 1 }, content: { $push: '$$ROOT' } }}",
        "{ $project: { _id: 0, totalElements: 1, content: { $slice: ['$content', ?3, ?4] } }}"
    })
    ShopOrderPageImpl findShopOrderThatProductNameContainingIgnoreCaseAndStatusInOrderByCreatedAtDesc(
            ObjectId shopId,
            String keyword,
            List<Integer> status,
            long offset,
            int limit
    );

}
