package com.app.bdc_backend.facade;

import com.app.bdc_backend.elasticsearch.service.ESProductService;
import com.app.bdc_backend.elasticsearch.service.ESShopOrderService;
import com.app.bdc_backend.model.dto.response.TaskOverviewDTO;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShopDashboardFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    private final ESShopOrderService esShopOrderService;

    private final ESProductService esProductService;

    public TaskOverviewDTO getTaskOverview() {
        TaskOverviewDTO dto = new TaskOverviewDTO();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        dto.setRestrictedProducts(esProductService.countRestrictedProductsOfShop(shop.getId().toString()));
        Map<Integer, Integer> shopOrderStatusCount = esShopOrderService.getShopOrderStatusCount(shop.getId().toString());
        dto.setPendingOrders(shopOrderStatusCount.getOrDefault(1, 0));
        dto.setPreparingOrders(shopOrderStatusCount.getOrDefault(2, 0));
        return dto;
    }


}
