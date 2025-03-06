package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.shop.ShopAddressRepository;
import com.app.bdc_backend.dao.shop.ShopCategoriesRepository;
import com.app.bdc_backend.dao.shop.ShopRepository;
import com.app.bdc_backend.model.dto.ShopPageImpl;
import com.app.bdc_backend.model.dto.response.AdminShopTableDTO;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.shop.ShopCategories;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<Shop> getAll(){
        return shopRepository.findAll();
    }

    public void saveAll(List<Shop> shops) {
        shopRepository.saveAll(shops);
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

    public Page<Shop> getShopListForAdmin(int filterType,
                                          String keyword,
                                          boolean active,
                                          Pageable pageable){
        switch (filterType){
            case 0:
                return shopRepository.findAllByDeleted(!active, pageable);
            case 1:
                return shopRepository.findAllByDeletedAndNameContainingIgnoreCase(!active, keyword, pageable);
            case 2:
                Sort sort = pageable.getSort();
                String sortBy = sort.get().toList().get(0).getProperty();
                Sort.Direction direction = sort.get().toList().get(0).getDirection();
                ShopPageImpl pageRes = shopRepository.findAllByDeletedAndOwnerNameContainingIgnoreCase(
                        !active,
                        keyword,
                        pageable.getOffset(),
                        pageable.getPageSize(),
                        sortBy,
                        direction == Sort.Direction.DESC ? -1 : 1);
                return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    public AdminShopTableDTO toAdminShopTableDTO(Shop shop) {
        AdminShopTableDTO dto = new AdminShopTableDTO();
        dto.setId(shop.getId().toString());
        dto.setName(shop.getName());
        dto.setDescription(shop.getDescription());
        dto.setAvatarUrl(shop.getAvatarUrl());
        dto.setDeleted(shop.isDeleted());
        dto.setAverageRating(shop.getAverageRating());
        dto.setProductCount(shop.getProductCount());
        dto.setCreatedAt(shop.getCreatedAt());
        dto.setRevenue(shop.getRevenue());
        AdminShopTableDTO.Owner owner = new AdminShopTableDTO.Owner();
        owner.setFullName(shop.getUser().getFullName());
        owner.setId(shop.getUser().getId().toString());
        dto.setOwner(owner);
        if(shop.isDeleted()) dto.setDeleteReason(shop.getUser().getDeleteReason());
        return dto;
    }

}
