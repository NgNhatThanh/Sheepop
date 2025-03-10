package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.shop.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    int countByShopId(String shopId);

}
