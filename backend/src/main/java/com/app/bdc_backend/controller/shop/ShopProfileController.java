package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.ShopFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateShopProfileDTO;
import com.app.bdc_backend.model.dto.response.ShopProfileDTO;
import com.app.bdc_backend.model.shop.ShopAddress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/profile")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class ShopProfileController {

    private final ShopFacadeService shopFacadeService;

    @GetMapping("/get")
    @Operation(summary = "Get shop profile information")
    public ResponseEntity<ShopProfileDTO> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShopProfileDTO shop = shopFacadeService.getShopProfile(username);
        return ResponseEntity.ok(shop);
    }

    @GetMapping("/get_address")
    @Operation(summary = "Get shop address")
    public ResponseEntity<ShopAddress> getAddress() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(shopFacadeService.getShopAddress(username));
    }

    @PostMapping("/update")
    @Operation(summary = "Update shop profile information")
    public ResponseEntity<ShopProfileDTO> updateProfile(@RequestBody @Valid UpdateShopProfileDTO dto) {
        ShopProfileDTO resProfile = shopFacadeService.updateShopProfile(dto);
        return ResponseEntity.ok(resProfile);
    }

    @PostMapping("/update_address")
    @Operation(summary = "Update shop address")
    public ResponseEntity<ShopAddress> updateAddress(@RequestBody @Valid AddAddressDTO dto) {
        return ResponseEntity.ok(shopFacadeService.setAddress(dto));
    }

}
