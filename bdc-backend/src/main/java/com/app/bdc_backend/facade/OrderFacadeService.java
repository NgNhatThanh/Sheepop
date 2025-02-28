package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.dto.request.OrderCancelationDTO;
import com.app.bdc_backend.model.dto.response.*;
import com.app.bdc_backend.model.enums.CommonEntity;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.service.redis.impl.CartRedisService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderFacadeService {

    private final OrderService orderService;

    private final UserService userService;

    private final CartRedisService cartRedisService;

    private final CartService cartService;

    private final UserAddressService userAddressService;

    private final ProductService productService;

    private final PaymentService paymentService;

    public Order placeOrder(Map<String, Object> body){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            cart = cartService.findByUser(user);
        }
        else{
            for(CartItem item : cart.getItems()){
                item.setProduct(productService.findById(item.getProduct().getId().toString()));
            }
        }
        List<CartItem> checkoutList = new ArrayList<>();
        for(CartItem item : cart.getItems()){
            if(item.isSelected()){
                if(item.getProduct().getShop().getUser().getUsername().equals(username)){
                    throw new RequestException("Invalid request: cannot buy your own product");
                }
                if(item.getQuantity() > cartService.getItemStock(item)){
                    throw new RequestException("Invalid request: exceeded quantity");
                }
                else{
                    checkoutList.add(item);
                }
            }
        }
        for(CartItem item : checkoutList){
            cart.getItems().remove(item);
            cartService.deleteItem(item);
        }
        cartRedisService.save(cart);
        cartService.save(cart);
        List<UserAddress> userAddresses = userAddressService.getAddressListByUser(username);
        Order order = new Order();
        UserAddress to = null;
        for(UserAddress userAddress : userAddresses){
            if(userAddress.getId().toString().equals(body.get("address_id").toString())){
                to = userAddress;
                break;
            }
        }
        order.setAddress(to);
        order.setUser(user);
        Object rawShops = body.get("shop_orders");
        if (rawShops == null) {
            throw new RequestException("Invalid request: shop orders data is missing");
        }
        List<Map<String, Object>> shops;
        try{
            shops = (List<Map<String, Object>>) body.get("shop_orders");
        }
        catch (ClassCastException e){
            throw new RequestException("Invalid request: invalid shop orders data");
        }
        long totalPay = 0;
        for(Map<String, Object> shop : shops){
            totalPay += (Integer) shop.get("total_price");
            totalPay += (Integer) shop.get("shipping_fee");
        }
        Payment payment = new Payment();
        payment.setCreatedAt(new Date());
        payment.setType(PaymentType.fromString(body.get("payment_type").toString()));
        if(payment.getType() != PaymentType.COD) payment.setStatus(PaymentStatus.PENDING);
        else payment.setStatus(PaymentStatus.COD);
        payment.setAmount(totalPay);
        paymentService.save(payment);
        order.setPayment(payment);
        order = orderService.save(order);
        List<ShopOrder> shopOrders = new ArrayList<>();
        for(Map<String, Object> shop : shops){
            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setUser(user);
            shopOrder.setOrder(order);
            shopOrder.setShippingFee((Integer) shop.get("shipping_fee"));
            String shopId = (String) shop.get("shop_id");
            List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem item : checkoutList){
                if(item.getProduct().getShop().getId().toString().equals(shopId)){
                    shopOrder.setShop(item.getProduct().getShop());
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(item.getProduct());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setAttributes(item.getAttributes());
                    orderItems.add(orderItem);
                    sellItem(item);
                }
            }
            orderService.saveAllItems(orderItems);
            shopOrder.setStatus(ShopOrderStatus.PENDING);
            shopOrder.setItems(orderItems);
            shopOrders.add(shopOrder);
        }
        orderService.saveAllShopOrders(shopOrders);
        return order;
    }

    public OrderPageResponse getOrderList(int type, int limit, int offset){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return switch (type) {
            case 0 -> getAllOrders(user, offset, limit);
            case 1 -> getPaymentPendingOrders(user, offset, limit);
            case 2 -> getOrderByShopOrderStatus(user,
                    List.of(ShopOrderStatus.PENDING),
                    offset,
                    limit);
            case 3 -> getOrderByShopOrderStatus(user,
                    List.of(ShopOrderStatus.PREPARING),
                    offset,
                    limit);
            case 4 -> getOrderByShopOrderStatus(user,
                    List.of(ShopOrderStatus.SENT, ShopOrderStatus.DELIVERING),
                    offset,
                    limit);
            case 5 -> getOrderByShopOrderStatus(user,
                    List.of(ShopOrderStatus.COMPLETED, ShopOrderStatus.RATED),
                    offset,
                    limit);
            case 6 ->getOrderByShopOrderStatus(user,
                    List.of(ShopOrderStatus.CANCELLED),
                    offset,
                    limit);
            default -> throw new RequestException("Invalid request");
        };
    }

    public ShopOrderDetailDTO getOrderDetail(String shopOrderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(shopOrder == null){
            throw new RequestException("Invalid request: order not found");
        }
        if(!shopOrder.getUser().equals(user)){
            throw new RequestException("Invalid request: user");
        }
        return toShopOrderDetailDTO(shopOrder);
    }

    private OrderPageResponse getOrderByShopOrderStatus(User user, List<Integer> status, int offset, int limit) {
        OrderPageResponse response = new OrderPageResponse();
        List<OrderDTO> orderDTOS = new ArrayList<>();
        List<ShopOrder> shopOrders = orderService.getShopOrderByStatus(user,
                status,
                offset,
                limit);
        for(ShopOrder shopOrder : shopOrders){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setShopOrders(List.of(toShopOrderDTO(shopOrder)));
            orderDTO.setCancelable(shopOrder.getStatus() < ShopOrderStatus.SENT);
            orderDTO.setCompleted(shopOrder.getStatus() == ShopOrderStatus.COMPLETED);
            orderDTO.setRated(shopOrder.getStatus() == ShopOrderStatus.RATED);
            orderDTO.setId(shopOrder.getOrder().getId().toString());
            orderDTO.setStatus(shopOrder.getStatus());
            orderDTO.setCreatedAt(shopOrder.getCreatedAt());
            orderDTO.setPayment(shopOrder.getOrder().getPayment());
            orderDTOS.add(orderDTO);
        }
        response.setDetailList(orderDTOS);
        response.setNextOffset(offset + shopOrders.size());
        return response;
    }

    public void markOrderAsReceived(String shopOrderId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(!shopOrder.getUser().equals(user)){
            throw new RequestException("Invalid request: user");
        }
        if(shopOrder.getStatus() != ShopOrderStatus.SENT
                && shopOrder.getStatus() != ShopOrderStatus.DELIVERING){
            throw new RequestException("Invalid request: status");
        }
        for(OrderItem item : shopOrder.getItems()){
            item.setSuccess(true);
        }
        orderService.saveAllItems(shopOrder.getItems());
        shopOrder.setStatus(ShopOrderStatus.COMPLETED);
        orderService.saveAllShopOrders(List.of(shopOrder));
    }

    public void cancelOrder(OrderCancelationDTO dto){
        if(dto.getCancelReason() == null || dto.getCancelReason().isEmpty()){
            throw new RequestException("Invalid request: cancel reason mustn't be empty");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShopOrder> cancelList = new ArrayList<>();
        if(dto.getWhoCancel() == CommonEntity.USER){
            if(dto.getOrderId() == null){
                throw new RequestException("Invalid request: order id mustn't be null");
            }
            Order order = orderService.getOrderById(dto.getOrderId());
            if(order == null){
                throw new RequestException("Invalid request: order not found");
            }
            if(!order.getUser().getUsername().equals(username)){
                throw new RequestException("Invalid request: user");
            }
            List<ShopOrder> shopOrders = orderService.getAllShopOrderByOrder(order);
            if(order.getPayment().getStatus() == PaymentStatus.PENDING){
                if(dto.getShopOrderIds().size() != shopOrders.size()){
                    throw new RequestException("Invalid request: invalid shop orders amount");
                }
                order.getPayment().setStatus(PaymentStatus.CANCELLED);
                cancelList = shopOrders;
            }
            else{
                for(ShopOrder shopOrder : shopOrders){
                    if(shopOrder.getId().toString().equals(dto.getShopOrderIds().get(0))){
                        cancelList = List.of(shopOrder);
                        break;
                    }
                }
                if(order.getPayment().getStatus() == PaymentStatus.COMPLETED){
                    refund();
                }
            }
        }
        else if(dto.getWhoCancel() == CommonEntity.SHOP){
            ShopOrder shopOrder = orderService.getShopOrderById(dto.getShopOrderIds().get(0));
            if(dto.getShopOrderIds().isEmpty()){
                throw new RequestException("Invalid request: shopOrderIds mustn't be empty");
            }
            if(shopOrder == null){
                throw new RequestException("Invalid request: shopOrder not found");
            }
            if(!shopOrder.getShop().getUser().getUsername().equals(username)){
                throw new RequestException("Invalid request: invalid shop owner");
            }
            if(shopOrder.getStatus() != ShopOrderStatus.PENDING){
                throw new RequestException("Invalid request: this order cannot be canceled");
            }
            cancelList = List.of(shopOrder);
        }
        else if(dto.getWhoCancel() == CommonEntity.ADMIN){
            // code for admin
        }
        else{
            throw new RequestException("Invalid request: entity");
        }
        orderService.cancelAllShopOrders(cancelList, dto.getWhoCancel(), dto.getCancelReason());
    }

    private void refund(){
        log.info("Need to refund");
    }


    private OrderPageResponse getPaymentPendingOrders(User user, int offset, int limit){
        List<Order> orders = orderService.getOrdersByPaymentStatus(user, PaymentStatus.PENDING, offset, limit);
        return toOrderPageResponse(orders, offset);
    }

    private OrderPageResponse getAllOrders(User user, int offset, int limit) {
        List<Order> orders = orderService.getLastByUser(user, offset, limit);
        return toOrderPageResponse(orders, offset);
    }

    private void sellItem(CartItem item){
        if(item.getProduct().getSkuList().isEmpty()){
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productService.saveProduct(product);
            return;
        }
        for(ProductSKU sku : item.getProduct().getSkuList()){
            if(new HashSet<>(sku.getAttributes()).containsAll(item.getAttributes())){
                sku.setQuantity(sku.getQuantity() - item.getQuantity());
                productService.saveSKU(sku);
                return;
            }
        }
    }

    private ShopOrderDetailDTO toShopOrderDetailDTO(ShopOrder shopOrder) {
        ShopOrderDetailDTO dto = ModelMapper.getInstance().map(shopOrder, ShopOrderDetailDTO.class);
        dto.setId(shopOrder.getId().toString());
        dto.setShopName(shopOrder.getShop().getName());
        dto.setShopUsername(shopOrder.getShop().getUser().getUsername());
        dto.setAddress(shopOrder.getOrder().getAddress());
        dto.setPayment(shopOrder.getOrder().getPayment());
        dto.setItems(shopOrder.getItems()
                .stream()
                .map(this::toOrderItemDTO)
                .toList());
        return dto;
    }

    private OrderPageResponse toOrderPageResponse(List<Order> orders, int offset){
        OrderPageResponse response = new OrderPageResponse();
        response.setNextOffset(offset + orders.size());
        List<OrderDTO> orderDtos = new ArrayList<>();
        for(Order order : orders){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId().toString());
            orderDTO.setCreatedAt(order.getCreatedAt());
            orderDTO.setPayment(order.getPayment());
            boolean pendingPayment = order.getPayment().getStatus() == PaymentStatus.PENDING
                    && order.getPayment().getType() != PaymentType.COD;
            orderDTO.setPending(pendingPayment);
            List<ShopOrder> shopOrders = orderService.getAllShopOrderByOrder(order);
            List<ShopOrderDTO> dtoShopOrders = new ArrayList<>();
            for(ShopOrder shopOrder : shopOrders){
                ShopOrderDTO dtoShopOrder = toShopOrderDTO(shopOrder);
                orderDTO.setStatus(shopOrder.getStatus());
                log.info("Shop order status: " + shopOrder.getStatus());
                if(shopOrder.getStatus() < ShopOrderStatus.SENT) orderDTO.setCancelable(true);
                if(!pendingPayment){
                    log.info("Status: " + shopOrder.getStatus());
                    orderDTO.setCompleted(shopOrder.getStatus() == ShopOrderStatus.COMPLETED);
                    orderDTO.setRated(shopOrder.getStatus() == ShopOrderStatus.RATED);
                    orderDTO.setShopOrders(List.of(dtoShopOrder));
                    orderDtos.add(orderDTO);
                }
                else dtoShopOrders.add(dtoShopOrder);
            }
            if(pendingPayment){
                orderDTO.setShopOrders(dtoShopOrders);
                orderDtos.add(orderDTO);
            }
        }
        response.setDetailList(orderDtos);
        return response;
    }

    private ShopOrderDTO toShopOrderDTO(ShopOrder shopOrder) {
        ShopOrderDTO dtoShopOrder = new ShopOrderDTO();
        dtoShopOrder.setId(shopOrder.getId().toString());
        dtoShopOrder.setUsername(shopOrder.getShop().getUser().getUsername());
        dtoShopOrder.setName(shopOrder.getShop().getName());
        dtoShopOrder.setStatus(shopOrder.getStatus());
        dtoShopOrder.setCreatedAt(shopOrder.getCreatedAt());
        dtoShopOrder.setShippingFee(shopOrder.getShippingFee());
        List<OrderItemDTO> itemDtos = shopOrder.getItems()
                .stream()
                .map(this::toOrderItemDTO).toList();
        dtoShopOrder.setItems(itemDtos);
        return dtoShopOrder;
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
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

}
