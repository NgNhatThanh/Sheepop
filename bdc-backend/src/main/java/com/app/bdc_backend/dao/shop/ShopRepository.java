package com.app.bdc_backend.dao.shop;

import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends MongoRepository<Shop, String> {

    Optional<Shop> findByUser(User user);



}
