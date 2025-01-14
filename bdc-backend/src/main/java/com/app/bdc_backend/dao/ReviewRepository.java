package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.product.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<ProductReview, String> {

    int countByShopId(String shopId);

}
