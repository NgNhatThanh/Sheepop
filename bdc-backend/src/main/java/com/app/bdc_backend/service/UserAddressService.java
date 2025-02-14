package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.UserAddressRepository;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    public List<UserAddress> getAddressListByUser(String username){
        return userAddressRepository.findByUsername(username);
    }

    public void save(UserAddress userAddress){
        userAddressRepository.save(userAddress);
    }

}
