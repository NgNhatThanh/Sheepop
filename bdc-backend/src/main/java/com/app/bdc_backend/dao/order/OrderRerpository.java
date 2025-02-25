package com.app.bdc_backend.dao.order;

import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRerpository extends MongoRepository<Order, String> {

    Window<Order> findLastByUserOrderByCreatedAtDesc(User user, OffsetScrollPosition position, Limit limit);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'payments', localField: 'payment', foreignField: '_id', as: 'paymentDetails' } }",
            "{ $unwind: '$paymentDetails' }",
            "{ $match: { 'user': ?0, 'paymentDetails.status': ?1 } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    List<Order> findLastByUserAndPayment_StatusOrderByCreatedAtDesc(
            ObjectId user,
            PaymentStatus paymentStatus,
            int offset,
            int limit
    );

}
