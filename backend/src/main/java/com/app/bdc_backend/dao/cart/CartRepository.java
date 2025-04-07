package com.app.bdc_backend.dao.cart;

import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<Cart, String>{

    Cart findByUser(User user);

}
