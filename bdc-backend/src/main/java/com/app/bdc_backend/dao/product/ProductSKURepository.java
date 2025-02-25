package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.product.ProductSKU;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSKURepository extends MongoRepository<ProductSKU, String> {
}
