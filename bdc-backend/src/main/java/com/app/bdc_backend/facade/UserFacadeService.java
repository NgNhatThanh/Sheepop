package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateProfileDTO;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.service.AddressService;
import com.app.bdc_backend.service.UserAddressService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;

    private final UserAddressService userAddressService;

    private final AddressService addressService;

    public UserResponseDTO getUserProfile(){
        String curUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(curUsername);
        UserResponseDTO response = ModelMapper.getInstance().map(user, UserResponseDTO.class);
        response.setId(user.getId().toString());
        return response;
    }

    public UserResponseDTO updateProfile(UpdateProfileDTO dto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!username.equals(dto.getUsername())){
            throw new RequestException("Invalid request: username");
        }
        User user = userService.findByUsername(username);
        user.setDob(dto.getDob());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setFullName(dto.getFullName());
        user.setGender(dto.getGender());
        userService.save(user);
        return ModelMapper.getInstance().map(user, UserResponseDTO.class);
    }

    public List<UserAddress> getAddressList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAddressService.getAddressListByUser(username);
    }

    public UserAddress addAddress(AddAddressDTO dto)  {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAddress userAddress;
        userAddress = fromDTOtoAddress(dto);
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
        return userAddress;
    }

    private UserAddress fromDTOtoAddress(AddAddressDTO dto)  {
        UserAddress userAddress = new UserAddress();
        userAddress.setPrimary(dto.isPrimary());
        userAddress.setDetail(dto.getDetail());
        userAddress.setPhoneNumber(dto.getPhoneNumber());
        userAddress.setReceiverName(dto.getReceiverName());
        Province province = addressService.findProvinceByName(dto.getProvince());
        if(province == null){
            throw new RequestException("Địa chỉ không hợp lệ");
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
            throw new RequestException("Địa chỉ không hợp lệ");
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
            throw new RequestException("Địa chỉ không hợp lệ");
        }
        return userAddress;
    }

}
