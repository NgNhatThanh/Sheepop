package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.ChangePasswordDTO;
import com.app.bdc_backend.model.dto.request.UpdateAddressDTO;
import com.app.bdc_backend.model.dto.request.UpdateProfileDTO;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.shop.Follow;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.service.user.FollowService;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserAddressService;
import com.app.bdc_backend.service.user.UserService;
import com.app.bdc_backend.util.ModelMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;

    private final UserAddressService userAddressService;

    private final AddressService addressService;

    private final FollowService followService;

    private final ShopService shopService;

    private final PasswordEncoder passwordEncoder;

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

    public void updateFollow(String shopId, boolean isFollow){
        Shop shop = shopService.findById(shopId);
        if(shop == null)
            throw new RequestException("Invalid request: shop not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if(user.isDeleted())
            throw new RequestException("Invalid request: shop is deleted");
        Follow follow = followService.find(shopId, user.getId().toString());
        boolean isFollowing = follow != null;
        if(isFollow && isFollowing)
            throw new RequestException("Invalid request: already followed");
        if(!isFollow && !isFollowing)
            throw new RequestException("Invalid request: already not followed");
        if(isFollow){
            follow = new Follow(shopId, user.getId().toString());
            followService.save(follow);
            shop.setFollowerCount(shop.getFollowerCount() + 1);
        }
        else{
            followService.delete(follow);
            shop.setFollowerCount(shop.getFollowerCount() - 1);
        }
        shopService.save(shop);
    }

    private UserAddress fromDTOtoAddress(AddAddressDTO dto)  {
        UserAddress userAddress = new UserAddress();
        userAddress.setPrimary(dto.isPrimary());
        userAddress.setDetail(dto.getDetail());
        userAddress.setPhoneNumber(dto.getPhoneNumber());
        userAddress.setReceiverName(dto.getReceiverName());
        userAddress = (UserAddress) addressService.setInfo(userAddress,
                dto.getProvince(),
                dto.getDistrict(),
                dto.getWard());
        return userAddress;
    }

    public void makeAddressPrimary(String addressId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserAddress> addresses = userAddressService.getAddressListByUser(username);
        boolean ok = false;
        List<UserAddress> needUpdate = new ArrayList<>();
        for(UserAddress address : addresses){
            if(address.getId().toString().equals(addressId)){
                address.setPrimary(true);
                needUpdate.add(address);
                ok = true;
            }
            else{
                if(address.isPrimary()){
                    address.setPrimary(false);
                    needUpdate.add(address);
                }
            }
        }
        if(!ok)
            throw new RequestException("Address not found");
        userAddressService.saveAll(needUpdate);
    }

    public void updateAddress(@Valid UpdateAddressDTO dto) {
        UserAddress address = userAddressService.getById(dto.getAddressId());
        if(address == null)
            throw new RequestException("Address not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!address.getUsername().equals(username))
            throw new RequestException("Address not found");
        if(address.isPrimary() && !dto.isPrimary())
            throw new RequestException("Cannot make a primary address non-primary");
        List<UserAddress> needUpdate = new ArrayList<>();
        if(!address.isPrimary() && dto.isPrimary()){
            List<UserAddress> addresses = userAddressService.getAddressListByUser(username);
            for(UserAddress addr : addresses){
                if(addr.isPrimary()){
                    addr.setPrimary(false);
                    needUpdate.add(addr);
                    break;
                }
            }
        }
        UserAddress updated = ModelMapper.getInstance().map(dto, UserAddress.class);
        updated = (UserAddress) addressService.setInfo(updated,
                dto.getProvince(),
                dto.getDistrict(),
                dto.getWard());
        updated.setUsername(address.getUsername());
        updated.setId(address.getId());
        needUpdate.add(updated);
        userAddressService.saveAll(needUpdate);
    }

    public void deleteAddress(String addressId) {
        UserAddress address = userAddressService.getById(addressId);
        if(address == null)
            throw new RequestException("Address not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!address.getUsername().equals(username))
            throw new RequestException("Address not found");
        if(address.isPrimary())
            throw new RequestException("Address is primary");
        userAddressService.delete(address);
    }

    public void changePassword(@Valid ChangePasswordDTO dto) {
        if(dto.getNewPassword().equals(dto.getOldPassword()))
            throw new RequestException("Old and new password are the same");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new RequestException("Old password is wrong");
        String hashedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashedPassword);
        userService.save(user);
    }
}
