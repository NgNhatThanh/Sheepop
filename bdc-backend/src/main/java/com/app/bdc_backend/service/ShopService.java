package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.ShopRepository;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public void addShop(Shop shop) {
        shopRepository.save(shop);
    }

    public Shop findByUser(User user) {
        return shopRepository.findByUser(user).orElse(null);
    }

    public Shop findById(String id) {
        return shopRepository.findById(id).orElse(null);
    }

}
