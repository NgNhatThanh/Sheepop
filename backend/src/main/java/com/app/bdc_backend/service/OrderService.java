package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.order.OrderItemRepository;
import com.app.bdc_backend.dao.order.OrderRerpository;
import com.app.bdc_backend.dao.order.PaymentRepository;
import com.app.bdc_backend.dao.shop.ShopOrderRepository;
import com.app.bdc_backend.model.dto.ShopOrderPageImpl;
import com.app.bdc_backend.model.dto.response.OrderItemDTO;
import com.app.bdc_backend.model.dto.response.ProductSaleInfo;
import com.app.bdc_backend.model.dto.response.ShopOrderDTO;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRerpository orderRerpository;

    private final OrderItemRepository orderItemRepository;

    private final ShopOrderRepository shopOrderRepository;

    private final PaymentRepository paymentRepository;

    public Order save(Order order) {
        return orderRerpository.save(order);
    }

    public Order getOrderById(String orderId) {
        return orderRerpository.findById(orderId).orElse(null);
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

    public Optional<ProductSaleInfo> getProductSaleInfo(ObjectId productId){
        return orderItemRepository.getProductSaleInfo(productId);
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

    public void cancelAllShopOrders(List<ShopOrder> shopOrders, int canceledBy, String reason){
        for(ShopOrder shopOrder : shopOrders){
            shopOrder.setCanceledBy(canceledBy);
            shopOrder.setCancelReason(reason);
            shopOrder.setStatus(ShopOrderStatus.CANCELLED);
        }
        saveAllShopOrders(shopOrders);
    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public List<ShopOrder> getShopOrderByStatus(User user, List<Integer> statusList, int offset, int limit){
        return shopOrderRepository.findLastByUserAndStatusInOrderByCreatedAtDesc(user.getId(),
                statusList,
                offset,
                limit);
    }

    public Page<ShopOrder> getShopOrderByShopAndStatus(Shop shop,
                                                       List<Integer> statusList,
                                                       Pageable pageable,
                                                       int filterType,
                                                       String keyword){
         if(filterType == 0)
             return shopOrderRepository.findLastByShopAndStatusIn(shop, statusList, pageable);
         else {
             if(filterType == 1)
                 return shopOrderRepository
                         .findLastByShopAndIdAndStatusIn(
                                 shop,
                                 keyword,
                                 statusList,
                                 pageable);
             else if(filterType == 2){
                 Sort sort = pageable.getSort();
                 String sortBy = sort.get().toList().get(0).getProperty();
                 Sort.Direction direction = sort.get().toList().get(0).getDirection();
                 ShopOrderPageImpl pageRes = shopOrderRepository.findByShopAndUserFullNameContainingIgnoreCaseAndStatusIn(
                         shop.getId(),
                         keyword,
                         statusList,
                         pageable.getOffset(),
                         pageable.getPageSize(),
                         sortBy,
                         direction == Sort.Direction.DESC ? -1 : 1
                 );
                 if(pageRes == null)
                     return new PageImpl<>(new ArrayList<>());
                 return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
             }
             else{
                Sort sort = pageable.getSort();
                String sortBy = sort.get().toList().get(0).getProperty();
                Sort.Direction direction = sort.get().toList().get(0).getDirection();
                ShopOrderPageImpl pageRes = shopOrderRepository.findShopOrderThatProductNameContainingIgnoreCaseAndStatusIn(
                        shop.getId(),
                        keyword,
                        statusList,
                        pageable.getOffset(),
                        pageable.getPageSize(),
                        sortBy,
                        direction == Sort.Direction.DESC ? -1 : 1
                );
                if(pageRes == null)
                    return new PageImpl<>(new ArrayList<>());
                return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
             }
         }
    }

    public Page<ShopOrder> getShopOrdersByStatus(List<Integer> statusList,
                                                 Pageable pageable,
                                                 int filterType,
                                                 String keyword){
        if(filterType == 0)
            return shopOrderRepository.findLastByStatusIn(statusList, pageable);
        else {
            if(filterType == 1)
                return shopOrderRepository
                        .findLastByIdAndStatusIn(
                                keyword,
                                statusList,
                                pageable);
            else if(filterType == 2){
                Sort sort = pageable.getSort();
                String sortBy = sort.get().toList().get(0).getProperty();
                Sort.Direction direction = sort.get().toList().get(0).getDirection();
                ShopOrderPageImpl pageRes = shopOrderRepository.findByShopAndUserFullNameContainingIgnoreCaseAndStatusIn(
                        null,
                        keyword,
                        statusList,
                        pageable.getOffset(),
                        pageable.getPageSize(),
                        sortBy,
                        direction == Sort.Direction.DESC ? -1 : 1
                );
                if(pageRes == null)
                    return new PageImpl<>(new ArrayList<>());
                return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
            }
            else if(filterType == 3){
                Sort sort = pageable.getSort();
                String sortBy = sort.get().toList().get(0).getProperty();
                Sort.Direction direction = sort.get().toList().get(0).getDirection();
                ShopOrderPageImpl pageRes = shopOrderRepository.findShopOrderThatProductNameContainingIgnoreCaseAndStatusIn(
                        null,
                        keyword,
                        statusList,
                        pageable.getOffset(),
                        pageable.getPageSize(),
                        sortBy,
                        direction == Sort.Direction.DESC ? -1 : 1
                );
                if(pageRes == null)
                    return new PageImpl<>(new ArrayList<>());
                return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
            }
            else{
                Sort sort = pageable.getSort();
                String sortBy = sort.get().toList().get(0).getProperty();
                Sort.Direction direction = sort.get().toList().get(0).getDirection();
                ShopOrderPageImpl pageRes = shopOrderRepository.findShopOrderThatShopnameContainingIgnoreCaseAndStatusIn(
                        null,
                        keyword,
                        statusList,
                        pageable.getOffset(),
                        pageable.getPageSize(),
                        sortBy,
                        direction == Sort.Direction.DESC ? -1 : 1
                );
                if(pageRes == null)
                    return new PageImpl<>(new ArrayList<>());
                return new PageImpl<>(pageRes.getContent(), pageable, pageRes.getTotalElements());
            }
        }
    }

    public List<ShopOrder> getAllShopOrder(){
        return shopOrderRepository.findAll();
    }

    public List<Payment> getAllPendingPayments() {
        return paymentRepository.findAllByStatusAndType(PaymentStatus.PENDING, PaymentType.BANK_TRANSFER);
    }

    public List<ShopOrder> getAllShopOrderByOrder(Order order){
        return shopOrderRepository.findByOrder(order);
    }

    public int countProductSold(ObjectId productId){
        Integer soldCount = orderItemRepository.countProductSoldByProductId(productId);
        return soldCount == null ? 0 : soldCount;
    }

    public ShopOrderDTO toShopOrderDTO(ShopOrder shopOrder) {
        ShopOrderDTO dtoShopOrder = new ShopOrderDTO();
        dtoShopOrder.setId(shopOrder.getId().toString());
        dtoShopOrder.setUsername(shopOrder.getShop().getUser().getUsername());
        dtoShopOrder.setName(shopOrder.getShop().getName());
        dtoShopOrder.setCompletedPayment(shopOrder.getOrder().getPayment().getStatus() == PaymentStatus.COMPLETED
        || shopOrder.getOrder().getPayment().getType() == PaymentType.COD);
        dtoShopOrder.setBuyerName(shopOrder.getUser().getFullName());
        dtoShopOrder.setBuyerUsername(shopOrder.getUser().getUsername());
        dtoShopOrder.setStatus(shopOrder.getStatus());
        dtoShopOrder.setCreatedAt(shopOrder.getCreatedAt());
        dtoShopOrder.setShippingFee(shopOrder.getShippingFee());
        dtoShopOrder.setTotal(shopOrder.getTotal());
        dtoShopOrder.setPaymentType(shopOrder.getOrder().getPayment().getType());
        List<OrderItemDTO> itemDtos = new ArrayList<>();
        for(OrderItem item : shopOrder.getItems()){
            OrderItemDTO dtoItem = toOrderItemDTO(item);
            itemDtos.add(dtoItem);
        }
        dtoShopOrder.setItems(itemDtos);
        return dtoShopOrder;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dtoItem = new OrderItemDTO();
        dtoItem.setId(item.getId().toString());
        dtoItem.setQuantity(item.getQuantity());
        dtoItem.setPrice(item.getPrice());
        dtoItem.setAttributes(item.getAttributes());
        dtoItem.getProduct().setId(item.getProduct().getId().toString());
        dtoItem.getProduct().setName(item.getProduct().getName());
        dtoItem.getProduct().setThumbnailUrl(item.getProduct().getThumbnailUrl());
        return dtoItem;
    }

    public List<Order> getAllOrderByPayment(List<Payment> expiredPayments) {
        return orderRerpository.findAllByPaymentIn(expiredPayments);
    }

    public void saveAllPayments(List<Payment> expiredPayments) {
        paymentRepository.saveAll(expiredPayments);
    }
}
