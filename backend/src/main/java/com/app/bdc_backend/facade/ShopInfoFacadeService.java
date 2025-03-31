package com.app.bdc_backend.facade;

import com.app.bdc_backend.elasticsearch.service.ESProductService;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.model.dto.response.ShopInfoDTO;
import com.app.bdc_backend.model.shop.Follow;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopCategories;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.user.FollowService;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopInfoFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    private final FollowService followService;

    private final ESProductService esProductService;

    public ShopInfoDTO getInfo(String username){
        User user = userService.findByUsername(username);
        if(user == null || user.isDeleted())
            throw new RequestException("Invalid request: shop not found");
        Shop shop = shopService.findByUser(user);
        ShopCategories shopCategories = shopService.getShopCategories(shop);
        return toShopInfoDTO(shop, shopCategories);
    }

    public Page<ProductCardDTO> getProductList(String shopId,
                                               String categoryId,
                                               String sortBy,
                                               String order,
                                               int page,
                                               int limit){
        return esProductService.getShopProductList(shopId, categoryId, sortBy, order, page, limit);
    }

    private ShopInfoDTO toShopInfoDTO(Shop shop, ShopCategories shopCategories){
        ShopInfoDTO dto = ModelMapper.getInstance().map(shop, ShopInfoDTO.class);
        dto.setId(shop.getId().toString());
        dto.setUserId(shop.getUser().getId().toString());
        dto.setShopCategories(shopCategories.getCategories());
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);
            Follow follow = followService.find(shop.getId().toString(), user.getId().toString());
            if(follow != null) dto.setFollowing(true);
        }
        return dto;
    }

}
