package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.AddressService;
import com.app.bdc_backend.service.UserAddressService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserAddressService userAddressService;

    private final AddressService addressService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        String curUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!curUsername.equals(username)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "You are not the current user"
            ));
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User not found"
            ));
        }
        UserResponseDTO response = ModelMapper.getInstance().map(user, UserResponseDTO.class);
        response.setId(user.getId().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/address/get-list")
    public ResponseEntity<?> getAddressList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userAddressService.getAddressListByUser(username));
    }

    @PostMapping("/address/add")
    public ResponseEntity<?> addAddress(@RequestBody AddAddressDTO dto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAddress userAddress;
        try{
            userAddress = fromDTOtoAddress(dto);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
        userAddress.setUsername(username);
        List<UserAddress> currentUserAddressList = userAddressService.getAddressListByUser(username);
        if(currentUserAddressList.isEmpty()) userAddress.setPrimary(true);
        else{
            if(userAddress.isPrimary()){
                for(UserAddress other : currentUserAddressList){
                    if(other.isPrimary()){
                        other.setPrimary(false);
                        userAddressService.save(other);
                        break;
                    }
                }
            }
        }
        userAddressService.save(userAddress);
        return ResponseEntity.ok(userAddress);
    }

    private UserAddress fromDTOtoAddress(AddAddressDTO dto){
        UserAddress userAddress = new UserAddress();
        userAddress.setPrimary(dto.isPrimary());
        userAddress.setDetail(dto.getDetail());
        userAddress.setPhoneNumber(dto.getPhoneNumber());
        userAddress.setReceiverName(dto.getReceiverName());
        Province province = addressService.findProvinceByName(dto.getProvince());
        if(province == null){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        userAddress.setProvince(province);
        List<District> districts = addressService.findDistrictByName(dto.getDistrict());
        boolean okDistrict = false;
        for(District d : districts){
            if(d.getProvinceId() == province.getId()){
                okDistrict = true;
                userAddress.setDistrict(d);
                break;
            }
        }
        if(!okDistrict){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        List<Ward> ward = addressService.findWardByName(dto.getWard());
        boolean okWard = false;
        for(Ward w : ward){
            if(w.getDistrictId() == userAddress.getDistrict().getId()){
                okWard = true;
                userAddress.setWard(w);
                break;
            }
        }
        if(!okWard){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        return userAddress;
    }

}
