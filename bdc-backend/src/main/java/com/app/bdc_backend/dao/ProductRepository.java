package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.shop.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    int countByShopId(ObjectId shopId);

    Page<Product> findByShop(Shop shop, Pageable pageable);

}
