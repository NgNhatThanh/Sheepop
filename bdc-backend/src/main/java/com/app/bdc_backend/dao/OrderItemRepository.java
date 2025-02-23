package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.order.OrderItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends MongoRepository<OrderItem, String> {

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'products', localField: 'product', foreignField: '_id', as: 'product_item' } }",
        "{ $unwind: '$product_item' }",
        "{ $match: { 'product_item._id': ?0, 'success': true } }",
        "{ $count: 'count' }"
    })
    int countProductSoldByProductId(ObjectId productId);

}
