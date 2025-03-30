package com.app.bdc_backend.facade.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.ShopService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminUserFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    private final ProductService productService;

    public Page<UserResponseDTO> getUserList(int type,
                                             int filterType,
                                             String keyword,
                                             int sortType,
                                             int page,
                                             int limit){
        if(filterType < 0 || filterType > 4)
            throw new RequestException("Invalid request: invalid filterType");
        if(sortType < 0 || sortType > 1)
            throw new RequestException("Invalid request: sort type");
        Sort sort = Sort.by(sortType == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<User> data = null;
        switch (type){
            case 0:
                data = userService.getUserListForAdmin(filterType, keyword, true, pageable);
                break;
            case 1:
                data = userService.getUserListForAdmin(filterType, keyword, false, pageable);
                break;
        }
        if(data == null)
            throw new RequestException("Invalid request: invalid type");
        return data.map(user -> ModelMapper.getInstance().map(user, UserResponseDTO.class));
    }

    public UserResponseDTO getUserProfile(String userId){
        User user = userService.findById(userId);
        if(user == null)
            throw new RequestException("Invalid request: user not found");
        return ModelMapper.getInstance().map(user, UserResponseDTO.class);
    }

    public void deleteUser(String userId, String reason){
        User user = userService.findById(userId);
        if(user == null)
            throw new RequestException("Invalid request: user not found");
        user.setDeleted(true);
        user.setDeleteReason(reason);
        Shop shop = shopService.findByUser(user);
        shop.setDeleted(true);
        List<Product> products = productService.getAllByShop(shop);
        for(Product product : products){
            product.setDeleted(true);
            product.setVisible(false);
        }
        shopService.save(shop);
        userService.save(user);
        productService.saveAllProducts(products);
    }

    public void restoreUser(String userId){
        User user = userService.findById(userId);
        if(user == null)
            throw new RequestException("Invalid request: user not found");
        user.setDeleted(false);
        Shop shop = shopService.findByUser(user);
        shop.setDeleted(false);
        List<Product> products = productService.getAllByShop(shop);
        for(Product product : products){
            product.setDeleted(false);
        }
        shopService.save(shop);
        userService.save(user);
        productService.saveAllProducts(products);
    }

}
