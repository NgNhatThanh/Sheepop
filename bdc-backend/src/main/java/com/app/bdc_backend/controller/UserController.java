package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.UserFacadeService;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateProfileDTO;
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
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDTO dto) {
        return ResponseEntity.ok(userFacadeService.updateProfile(dto));
    }

    @GetMapping("/address/get-list")
    public ResponseEntity<?> getAddressList() {
        return ResponseEntity.ok(userFacadeService.getAddressList());
    }

    @PostMapping("/address/add")
    public ResponseEntity<?> addAddress(@RequestBody AddAddressDTO dto){
        return ResponseEntity.ok(userFacadeService.addAddress(dto));
    }

}
