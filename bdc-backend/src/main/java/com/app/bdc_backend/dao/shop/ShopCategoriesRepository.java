package com.app.bdc_backend.dao.shop;

import com.app.bdc_backend.model.shop.ShopCategories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCategoriesRepository extends MongoRepository<ShopCategories, String> {
    ShopCategories findAllByShopId(String string);
}
