package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends MongoRepository<UserAddress, String> {

    List<UserAddress> findByUsername(String username);

}
