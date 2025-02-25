package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.model.dto.response.OrderItemDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ShopOrderDTO;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/order")
public class ShopOrderController {

    private final UserService userService;

    private final ShopService shopService;

    private final OrderService orderService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getShopOrderList(@RequestParam(value = "type") int type,
                                              @RequestParam(value = "filterType", defaultValue = "0") int filterType,
                                              @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                              @RequestParam(value = "page") int page,
                                              @RequestParam (value = "limit") int limit){
        if(filterType < 0 || filterType > 3){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: filter type"
            ));
        }
        if(filterType >= 1 && (keyword == null || keyword.isEmpty())){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: filter data"
            ));
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit);
        Page<ShopOrder> data = null;
        switch (type){
            case 0:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        ShopOrderStatus.getAllStatuses(),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 1:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PENDING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 2:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PREPARING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 3:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.SENT, ShopOrderStatus.DELIVERING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 4:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.COMPLETED, ShopOrderStatus.RATED),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 5:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.CANCELLED),
                        pageable,
                        filterType,
                        keyword);
                break;
        }
        if(data == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request"
            ));
        }
        Page<ShopOrderDTO> dtos = data.map(this::toShopOrderDTO);
        PageResponse<ShopOrderDTO> response = new PageResponse<>(dtos);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrderStatus(@RequestParam(value = "shopOrderId") String shopOrderId,
                                               @RequestParam(value = "currentStatus") int currentStatus){
        if(currentStatus > 2){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request"
            ));
        }
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(shopOrder == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid shop order id"
            ));
        }
        if(shopOrder.getStatus() != currentStatus){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid current status"
            ));
        }
        if(currentStatus == ShopOrderStatus.PENDING){
            shopOrder.setStatus(ShopOrderStatus.PREPARING);
        }
        else{
            shopOrder.setStatus(ShopOrderStatus.DELIVERING);
        }
        orderService.saveAllShopOrders(List.of(shopOrder));
        return ResponseEntity.ok().build();
    }

    private ShopOrderDTO toShopOrderDTO(ShopOrder shopOrder) {
        ShopOrderDTO dtoShopOrder = new ShopOrderDTO();
        dtoShopOrder.setId(shopOrder.getId().toString());
        dtoShopOrder.setUsername(shopOrder.getShop().getUser().getUsername());
        dtoShopOrder.setName(shopOrder.getShop().getName());
        dtoShopOrder.setCompletedPayment(shopOrder.getOrder().getPayment().getStatus() != PaymentStatus.PENDING
                || shopOrder.getOrder().getPayment().getType() == PaymentType.COD);
        dtoShopOrder.setStatus(shopOrder.getStatus());
        dtoShopOrder.setCreatedAt(shopOrder.getCreatedAt());
        dtoShopOrder.setShippingFee(shopOrder.getShippingFee());
        List<OrderItemDTO> itemDtos = new ArrayList<>();
        for(OrderItem item : shopOrder.getItems()){
            OrderItemDTO dtoItem = new OrderItemDTO();
            dtoItem.setId(item.getId().toString());
            dtoItem.setQuantity(item.getQuantity());
            dtoItem.setPrice(item.getPrice());
            dtoItem.setAttributes(item.getAttributes());
            dtoItem.getProduct().setId(item.getProduct().getId().toString());
            dtoItem.getProduct().setName(item.getProduct().getName());
            dtoItem.getProduct().setThumbnailUrl(item.getProduct().getThumbnailUrl());
            itemDtos.add(dtoItem);
        }
        dtoShopOrder.setItems(itemDtos);
        return dtoShopOrder;
    }

}
