package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.UserFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.ChangePasswordDTO;
import com.app.bdc_backend.model.dto.request.UpdateAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateProfileDTO;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.user.UserAddress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class UserController {

   private final UserFacadeService userFacadeService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile information")
    public ResponseEntity<UserResponseDTO> getUserInfo() {
        return ResponseEntity.ok(userFacadeService.getUserProfile());
    }

    @PostMapping("/profile/update")
    @Operation(summary = "Update user profile information")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody @Valid UpdateProfileDTO dto) {
        return ResponseEntity.ok(userFacadeService.updateProfile(dto));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change user's password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        userFacadeService.changePassword(dto);
        return ResponseEntity.ok().body(Map.of(
                "status", "success"
        ));
    }

    @GetMapping("/address/get-list")
    @Operation(summary = "Get list of user addresses")
    public ResponseEntity<List<UserAddress>> getAddressList() {
        return ResponseEntity.ok(userFacadeService.getAddressList());
    }

    @PostMapping("/address/add")
    @Operation(summary = "Add new address")
    public ResponseEntity<UserAddress> addAddress(@RequestBody @Valid AddAddressDTO dto){
        return ResponseEntity.ok(userFacadeService.addAddress(dto));
    }

    @PostMapping("/address/set-primary")
    @Operation(summary = "Set an address as primary")
    public ResponseEntity<Map<String, String>> makeAddressPrimary(@RequestParam String addressId){
        userFacadeService.makeAddressPrimary(addressId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/address/update")
    @Operation(summary = "Update address information")
    public ResponseEntity<Map<String, String>> updateAddress(@RequestBody @Valid UpdateAddressDTO dto){
        userFacadeService.updateAddress(dto);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/address/delete")
    @Operation(summary = "Delete an address")
    public ResponseEntity<Map<String, String>> deleteAddress(@RequestParam String addressId){
        userFacadeService.deleteAddress(addressId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PostMapping("/follow")
    @Operation(summary = "Follow a shop")
    public ResponseEntity<Map<String, String>> followShop(@RequestParam String shopId){
        userFacadeService.updateFollow(shopId, true);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/unfollow")
    @Operation(summary = "Unfollow a shop")
    public ResponseEntity<Map<String, String>> unfollowShop(@RequestParam String shopId){
        userFacadeService.updateFollow(shopId, false);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }
}
