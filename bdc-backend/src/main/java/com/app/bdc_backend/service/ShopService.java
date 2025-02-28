package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.shop.ShopAddressRepository;
import com.app.bdc_backend.dao.shop.ShopRepository;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    private final ShopAddressRepository shopAddressRepository;

    public void save(Shop shop) {
        shopRepository.save(shop);
    }

    public Shop findByUser(User user) {
        return shopRepository.findByUser(user).orElse(null);
    }

    public Shop findById(String id) {
        return shopRepository.findById(id).orElse(null);
    }

    public ShopAddress findAddressByShopId(String shopId){
        return shopAddressRepository.findByShopId(shopId);
    }

    public void saveAddress(ShopAddress shopAddress) {
        shopAddressRepository.save(shopAddress);
    }

}
