package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.shop.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    int countByShopAndVisibleAndDeleted(ObjectId shopId, boolean visible, boolean deleted);

    Page<Product> findByShopAndDeleted(Shop shop, boolean deleted, Pageable pageable);

    Page<Product> findAllByVisibleAndDeleted(boolean visible, boolean deleted, Pageable pageable);

    int countByCategory(Category category);
}
