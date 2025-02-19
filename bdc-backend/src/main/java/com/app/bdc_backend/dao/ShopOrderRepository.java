package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
