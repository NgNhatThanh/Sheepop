package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.product.ProductReviewMedia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewMediaRepository extends MongoRepository<ProductReviewMedia, String> {


}
