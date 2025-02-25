package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.AddProductDTO;
import com.app.bdc_backend.model.dto.response.*;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final UserService userService;

    private final ShopService shopService;

    private final AddressService addressService;

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

    @PostMapping("/address/save")
    public ResponseEntity<?> saveAddress(@RequestBody AddAddressDTO addressDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        ShopAddress address = shopService.findAddressByShopId(shop.getId().toString());
        if(address == null) address = fromDTOtoAddress(addressDTO);
        else{
            ShopAddress tmp = fromDTOtoAddress(addressDTO);
            address.setProvince(tmp.getProvince());
            address.setDistrict(tmp.getDistrict());
            address.setWard(tmp.getWard());
            address.setDetail(tmp.getDetail());
        }
        address.setShopId(shop.getId().toString());
        shopService.saveAddress(address);
        return ResponseEntity.ok(address);
    }

    private ShopAddress fromDTOtoAddress(AddAddressDTO dto){
        ShopAddress address = new ShopAddress();
        address.setDetail(dto.getDetail());
        Province province = addressService.findProvinceByName(dto.getProvince());
        if(province == null){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        address.setProvince(province);
        List<District> districts = addressService.findDistrictByName(dto.getDistrict());
        boolean okDistrict = false;
        for(District d : districts){
            if(d.getProvinceId() == province.getId()){
                okDistrict = true;
                address.setDistrict(d);
                break;
            }
        }
        if(!okDistrict){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        List<Ward> ward = addressService.findWardByName(dto.getWard());
        boolean okWard = false;
        for(Ward w : ward){
            if(w.getDistrictId() == address.getDistrict().getId()){
                okWard = true;
                address.setWard(w);
                break;
            }
        }
        if(!okWard){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        return address;
    }

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
