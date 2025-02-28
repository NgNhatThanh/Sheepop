package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopFacadeService shopFacadeService;

//    @GetMapping("/info/{username}")
//    public ResponseEntity<?> getShopInfo(@PathVariable String username) {
//        User user = userService.findByUsername(username);
//        if(user == null){
//            return ResponseEntity.badRequest().body(Map.of(
//                    "message", "User not found!"
//            ));
//        }
//        Shop shop = shopService.findByUser(user);
//        if(shop == null){
//            return ResponseEntity.badRequest().body(Map.of(
//                    "message", "User doesn't have a shop"
//            ));
//        }
//        return ResponseEntity.ok(toShopResponseDTO(shop));
//    }
//
//    @GetMapping("/base/{shopId}")
//    public ResponseEntity<?> getShopBase(@PathVariable String shopId) {
//        Shop shop = shopService.findById(shopId);
//        if(shop == null){
//            return ResponseEntity.badRequest().body(Map.of(
//                    "message", "Shop not found!"
//            ));
//        }
//        return ResponseEntity.ok(toShopResponseDTO(shop));
//    }


//    private ShopResponseDTO toShopResponseDTO(Shop shop) {
//        int followerCount = followService.getShopFollowCount(shop.getId().toString());
//        int productCount = productService.countProductOfShop(shop.getId().toString());
//        Map<String, String> ratingInfo = reviewService.getCountAndAverageReviewOfShop(shop.getId().toString());
//        ShopResponseDTO dto = ModelMapper.getInstance().map(shop, ShopResponseDTO.class);
//        dto.setShopId(shop.getId().toString());
//        dto.setFollowerCount(followerCount);
//        dto.setProductCount(productCount);
//        dto.setReviewCount(Integer.parseInt(ratingInfo.get("reviewCount")));
//        dto.setAverageRating(Float.parseFloat(ratingInfo.get("averageRating")));
//        return dto;
//    }

}
