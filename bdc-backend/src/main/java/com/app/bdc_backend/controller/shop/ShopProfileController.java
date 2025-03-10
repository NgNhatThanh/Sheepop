package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateShopProfileDTO;
import com.app.bdc_backend.model.dto.response.ShopProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/profile")
public class ShopProfileController {

    private final ShopFacadeService shopFacadeService;

    @GetMapping("/get")
    public ResponseEntity<?> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShopProfileDTO shop = shopFacadeService.getShopProfile(username);
        return ResponseEntity.ok(shop);
    }

    @GetMapping("/get_address")
    public ResponseEntity<?> getAddress() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(shopFacadeService.getShopAddress(username));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateShopProfileDTO dto) {
        ShopProfileDTO resProfile = shopFacadeService.updateShopProfile(dto);
        return ResponseEntity.ok(resProfile);
    }

    @PostMapping("/update_address")
    public ResponseEntity<?> updateAddress(@RequestBody AddAddressDTO dto) {
        return ResponseEntity.ok(shopFacadeService.setAddress(dto));
    }

}
