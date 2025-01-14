package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.dto.request.ShopCreateDTO;
import com.app.bdc_backend.model.dto.response.ShopResponseDTO;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final UserService userService;

    private final ShopService shopService;

    private final FollowService followService;

    private final ProductService productService;

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestBody ShopCreateDTO createDTO) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Shop shop = shopService.findByUser(user);
        if(shop != null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Shop already exists!"
            ));
        }
        shop = new Shop();
        shop.setUser(user);
        shop.setName(createDTO.getName());
        shop.setDescription(createDTO.getDescription());
        shop.setCreatedAt(new Date());
        shop.setAvatarUrl(createDTO.getAvatarUrl());
        shopService.addShop(shop);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info/{username}")
    public ResponseEntity<?> getShopInfo(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if(user == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User not found!"
            ));
        }
        Shop shop = shopService.findByUser(user);
        if(shop == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User doesn't have a shop"
            ));
        }
        return ResponseEntity.ok(toShopResponseDTO(shop));
    }

    @GetMapping("/base/{shopId}")
    public ResponseEntity<?> getShopBase(@PathVariable String shopId) {
        Shop shop = shopService.findById(shopId);
        if(shop == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Shop not found!"
            ));
        }
        return ResponseEntity.ok(toShopResponseDTO(shop));
    }

    private ShopResponseDTO toShopResponseDTO(Shop shop) {
        int followerCount = followService.getShopFollowCount(shop.getId().toString());
        int productCount = productService.countProductOfShop(shop.getId().toString());
        Map<String, String> ratingInfo = reviewService.getCountAndAverageReviewOfShop(shop.getId().toString());
        ShopResponseDTO dto = ModelMapper.getInstance().map(shop, ShopResponseDTO.class);
        dto.setShopId(shop.getId().toString());
        dto.setFollowerCount(followerCount);
        dto.setProductCount(productCount);
        dto.setReviewCount(Integer.parseInt(ratingInfo.get("reviewCount")));
        dto.setAverageRating(Float.parseFloat(ratingInfo.get("averageRating")));
        return dto;
    }

}
