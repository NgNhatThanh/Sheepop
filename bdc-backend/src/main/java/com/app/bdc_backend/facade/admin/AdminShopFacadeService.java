package com.app.bdc_backend.facade.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.response.AdminShopTableDTO;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.service.ShopService;
import com.app.bdc_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminShopFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    public Page<AdminShopTableDTO> getShopList(int type,
                                               int filterType,
                                               String keyword,
                                               int sortType,
                                               int page,
                                               int limit){
        if(filterType < 0 || filterType > 2)
            throw new RequestException("Invalid request: invalid filterType");
        if(sortType < 0 || sortType > 7)
            throw new RequestException("Invalid request: sort type");
        String sortBy;
        if(sortType < 2) sortBy = "createdAt";
        else if(sortType < 4) sortBy = "revenue";
        else if(sortType < 6) sortBy = "productCount";
        else sortBy = "averageRating";
        Sort sort = Sort.by(sortType % 2 == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Shop> data = null;
        switch (type){
            case 0:
                data = shopService.getShopListForAdmin(filterType, keyword, true, pageable);
                break;
            case 1:
                data = shopService.getShopListForAdmin(filterType, keyword, false, pageable);
                break;
        }
        if(data == null)
            throw new RequestException("Invalid request: invalid type");
        return data.map(shopService::toAdminShopTableDTO);
    }

}
