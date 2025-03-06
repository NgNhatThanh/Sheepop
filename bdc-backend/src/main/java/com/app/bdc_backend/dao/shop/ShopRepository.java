package com.app.bdc_backend.dao.shop;

import com.app.bdc_backend.model.dto.ShopPageImpl;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import org.apache.hc.core5.http2.impl.nio.ServerH2IOEventHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends MongoRepository<Shop, String> {

    Optional<Shop> findByUser(User user);

    Page<Shop> findAllByDeleted(boolean deleted, Pageable pageable);

    Page<Shop> findAllByDeletedAndNameContainingIgnoreCase(boolean deleted, String name, Pageable pageable);

    @Aggregation(pipeline = {
        "{ $match: { deleted: ?0 } }",
        "{ $lookup: { from: 'users', localField: 'user', foreignField: '_id', as: 'owner' } }",
        "{ $match: { 'owner.fullName': { $regex: ?1, $options: 'i' } } }",
        "{ $sort: { ?4: ?5 } }",
        "{ $group: { _id: null, totalElements: { $sum: 1 }, content: { $push: '$$ROOT' }}}",
        "{ $project: { _id: 0, totalElements: 1, content: { $slice: ['$content', ?2, ?3] } }}"
    })
    ShopPageImpl findAllByDeletedAndOwnerNameContainingIgnoreCase(boolean deleted,
                                                                  String name,
                                                                  long offset,
                                                                  int limit,
                                                                  String sortBy,
                                                                  int direction);

}
