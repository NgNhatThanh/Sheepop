package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.UserFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

   private final UserFacadeService userFacadeService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok(userFacadeService.getUserProfile());
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UpdateProfileDTO dto) {
        return ResponseEntity.ok(userFacadeService.updateProfile(dto));
    }

    @GetMapping("/address/get-list")
    public ResponseEntity<?> getAddressList() {
        return ResponseEntity.ok(userFacadeService.getAddressList());
    }

    @PostMapping("/address/add")
    public ResponseEntity<?> addAddress(@RequestBody @Valid AddAddressDTO dto){
        return ResponseEntity.ok(userFacadeService.addAddress(dto));
    }

    @PostMapping("/address/set-primary")
    public ResponseEntity<?> makeAddressPrimary(@RequestParam String addressId){
        userFacadeService.makeAddressPrimary(addressId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/address/update")
    public ResponseEntity<?> updateAddress(@RequestBody @Valid UpdateAddressDTO dto){
        userFacadeService.updateAddress(dto);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/address/delete")
    public ResponseEntity<?> deleteAddress(@RequestParam String addressId){
        userFacadeService.deleteAddress(addressId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followShop(@RequestParam String shopId){
        userFacadeService.updateFollow(shopId, true);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowShop(@RequestParam String shopId){
        userFacadeService.updateFollow(shopId, false);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

}
