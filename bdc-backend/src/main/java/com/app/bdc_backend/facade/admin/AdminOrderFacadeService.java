package com.app.bdc_backend.facade.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.response.ShopOrderDTO;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ShopService;
import com.app.bdc_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminOrderFacadeService {

    private final OrderService orderService;

    public Page<ShopOrderDTO> getOrderList(int type,
                                            int filterType,
                                            String keyword,
                                            int sortType,
                                            int page,
                                            int limit){
        if(filterType < 0 || filterType > 3){
            throw new RequestException("Invalid request: filter type");
        }
        if(filterType >= 1 && (keyword == null || keyword.isEmpty())){
            throw new RequestException("Invalid request: filter data");
        }
        if(sortType < 0 || sortType > 3)
            throw new RequestException("Invalid request: sort type");
        Sort sort;
        String sortBy;
        if(sortType < 2) sortBy = "createdAt";
        else sortBy = "total";
        sort = Sort.by(sortType % 2 == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<ShopOrder> data = null;
        switch (type){
            case 0:
                data = orderService.getShopOrdersByStatus(ShopOrderStatus.getAllStatuses(),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 1:
                data = orderService.getShopOrdersByStatus(List.of(ShopOrderStatus.PENDING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 2:
                data = orderService.getShopOrdersByStatus(List.of(ShopOrderStatus.PREPARING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 3:
                data = orderService.getShopOrdersByStatus(List.of(ShopOrderStatus.SENT, ShopOrderStatus.DELIVERING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 4:
                data = orderService.getShopOrdersByStatus(List.of(ShopOrderStatus.COMPLETED, ShopOrderStatus.RATED),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 5:
                data = orderService.getShopOrdersByStatus(List.of(ShopOrderStatus.CANCELLED),
                        pageable,
                        filterType,
                        keyword);
                break;
        }
        if(data == null){
            throw new RequestException("Invalid request");
        }
        return data.map(orderService::toShopOrderDTO);
    }

}
