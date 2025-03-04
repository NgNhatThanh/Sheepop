package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.shop.ShopAddressRepository;
import com.app.bdc_backend.dao.shop.ShopCategoriesRepository;
import com.app.bdc_backend.dao.shop.ShopRepository;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.shop.ShopCategories;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    private final ShopAddressRepository shopAddressRepository;

    private final ShopCategoriesRepository shopCategoriesRepository;

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

    public ShopCategories getShopCategories(Shop shop) {
        ShopCategories shopCategories = shopCategoriesRepository.findAllByShopId(shop.getId().toString());
        if(shopCategories == null){
            shopCategories = new ShopCategories();
            shopCategories.setShopId(shop.getId().toString());
            shopCategoriesRepository.save(shopCategories);
        }
        return shopCategories;
    }

}
