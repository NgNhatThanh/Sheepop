package com.app.bdc_backend.dao.shop;

import com.app.bdc_backend.model.shop.ShopAddress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopAddressRepository extends MongoRepository<ShopAddress, String> {

    ShopAddress findByShopId(String shopId);

}
