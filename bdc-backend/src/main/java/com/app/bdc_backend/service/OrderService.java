package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.OrderItemRepository;
import com.app.bdc_backend.dao.OrderRerpository;
import com.app.bdc_backend.dao.ShopOrderRepository;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRerpository orderRerpository;

    private final OrderItemRepository orderItemRepository;

    private final ShopOrderRepository shopOrderRepository;

    public Order save(Order order) {
        return orderRerpository.save(order);
    }

    public void saveAllItems(List<OrderItem> items){
        orderItemRepository.saveAll(items);
    }

    public void saveAllShopOrders(List<ShopOrder> shopOrders){
        shopOrderRepository.saveAll(shopOrders);
    }

    public ShopOrder getShopOrderById(String id){
        return shopOrderRepository.findById(id).orElse(null);
    }

    public List<Order> getLastByUser(User user, int offset, int limit){
        return orderRerpository.findLastByUserOrderByCreatedAtDesc(user,
                        offset > 0 ? ScrollPosition.offset(offset - 1) : ScrollPosition.offset(),
                        Limit.of(limit))
                .stream().toList();
    }

    public List<Order> getOrdersByPaymentStatus(User user, PaymentStatus paymentStatus, int offset, int limit){
        return orderRerpository.findLastByUserAndPayment_StatusOrderByCreatedAtDesc(user.getId(),
                        paymentStatus,
                offset,
                limit)
                .stream().toList();
    }

    public List<ShopOrder> getShopOrderByStatus(User user, List<Integer> statusList, int offset, int limit){
        return shopOrderRepository.findLastByUserAndStatusInOrderByCreatedAtDesc(user.getId(),
                statusList,
                offset,
                limit);
    }

    public Page<ShopOrder> getShopOrderByShopAndStatus(Shop shop, List<Integer> statusList, Pageable pageable){
        return shopOrderRepository.findLastByShopAndStatusInOrderByCreatedAtDesc(shop, statusList, pageable);
    }

    public List<ShopOrder> findByOrder(Order order){
        return shopOrderRepository.findByOrder(order);
    }

    public Page<ShopOrder> getShopOrderByShop(Shop shop, Pageable pageable){
        return shopOrderRepository.findLastByShopOrderByCreatedAtDesc(shop, pageable);
    }

    public int countProductSold(ObjectId productId){
        return orderItemRepository.countProductSoldByProductId(productId);
    }

    public int countShopSold(ObjectId shopId){
        return shopOrderRepository.countShopSold(shopId);
    }

}
