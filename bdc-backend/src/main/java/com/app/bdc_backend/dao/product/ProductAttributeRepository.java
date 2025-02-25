package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.product.ProductAttribute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends MongoRepository<ProductAttribute, String> {
}
