package com.app.bdc_backend.dao.cart;

import com.app.bdc_backend.model.cart.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {
}
