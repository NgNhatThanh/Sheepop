package com.app.bdc_backend.facade;

import com.app.bdc_backend.model.BasicShippingOrderInfo;
import com.app.bdc_backend.model.ShipmentInfo;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.dto.response.CheckoutDTO;
import com.app.bdc_backend.model.dto.response.ShopCheckoutDTO;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.service.redis.impl.CartRedisService;
import com.app.bdc_backend.service.shipment.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CheckoutFacaceService {

    private final UserService userService;

    private final ProductService productService;

    private final CartRedisService cartRedisService;

    private final CartService cartService;

    private final GHNService ghnService;

    private final ShopService shopService;

    private final UserAddressService userAddressService;
    
    public CheckoutDTO getCheckoutList(Map<String, Object> rqBody){
        Map<String, Object> body = new HashMap<>();
        if(rqBody != null) body = rqBody;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            User user = userService.findByUsername(username);
            cart = cartService.findByUser(user);
        }
        else{
            List<CartItem> uptList = new ArrayList<>();
            for(CartItem item : cart.getItems()){
                item.setProduct(productService.findById(item.getProduct().getId().toString()));
                if(item.getQuantity() > getItemStock(item)){
                    item.setSelected(false);
                }
                uptList.add(item);
            }
            cartService.saveAllItem(uptList);
        }
        cartRedisService.save(cart);
        List<CartItem> selectedItem = new ArrayList<>();
        for(CartItem item : cart.getItems()){
            if(item.isSelected()) selectedItem.add(item);
        }
        cart.setItems(selectedItem);
        return toCheckoutDTO(cart, body);
    }

    private CheckoutDTO toCheckoutDTO(Cart cart, Map<String, Object> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CheckoutDTO dto = new CheckoutDTO();

        Map<String, ShopCheckoutDTO> mp = new HashMap<>();
        UserAddress to = null;
        List<UserAddress> userAddresses = userAddressService.getAddressListByUser(username);
        String addressId = body.get("addressId") != null ? body.get("addressId").toString() : null;
        if(addressId == null) {
            for(UserAddress add : userAddresses){
                if(add.isPrimary()){
                    to = add;
                    break;
                }
            }
        }
        else{
            for(UserAddress add : userAddresses){
                if(add.getId().toString().equals(addressId)){
                    to = add;
                    break;
                }
            }
        }

        for(CartItem item : cart.getItems()){
            mp.putIfAbsent(item.getProduct().getShop().getId().toString(), new ShopCheckoutDTO());
            ShopCheckoutDTO shopCheckoutDTO = mp.get(item.getProduct().getShop().getId().toString());
            if(shopCheckoutDTO.getShop() == null){
                ShopCheckoutDTO.ShopDTO shopDTO = new ShopCheckoutDTO.ShopDTO();
                shopDTO.setId(item.getProduct().getShop().getId().toString());
                shopDTO.setName(item.getProduct().getShop().getName());
                shopDTO.setUsername(item.getProduct().getShop().getUser().getUsername());
                shopCheckoutDTO.setShop(shopDTO);
            }
            ShopCheckoutDTO.ShopCheckoutItem checkoutItem = toShopCheckoutItem(item);
            shopCheckoutDTO.getItems().add(checkoutItem);
            mp.put(item.getProduct().getShop().getId().toString(), shopCheckoutDTO);
        }

        for(ShopCheckoutDTO shopCheckoutDTO : mp.values()){
            ShopAddress shopAddress = shopService.findAddressByShopId(shopCheckoutDTO.getShop().getId());
            ShipmentInfo shipmentInfo = new ShipmentInfo();
            shipmentInfo.setFrom(shopAddress);
            shipmentInfo.setTo(to);
            int totalWeight = 0;
            for(CartItem cartItem : cart.getItems()){
                if(cartItem.getProduct().getShop().getId().toString().equals(shopCheckoutDTO.getShop().getId())){
                    totalWeight += cartItem.getProduct().getWeight() * cartItem.getQuantity();
                }
            }
            shipmentInfo.setWeight(totalWeight);
            try{
                BasicShippingOrderInfo orderInfo = ghnService.calculateShipmentFee(shipmentInfo);
                shopCheckoutDTO.setShipmentFee(orderInfo.getFee());
                shopCheckoutDTO.setExpectedDeliveryDate(orderInfo.getExpectedDeliveryDate());
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        dto.setShopCheckouts(mp.values().stream().toList());
        return dto;
    }

    private static ShopCheckoutDTO.ShopCheckoutItem toShopCheckoutItem(CartItem item) {
        ShopCheckoutDTO.ShopCheckoutItem checkoutItem = new ShopCheckoutDTO.ShopCheckoutItem();
        checkoutItem.setItemId(item.getId().toString());
        checkoutItem.setQuantity(item.getQuantity());
        checkoutItem.setSelected(item.isSelected());
        checkoutItem.setProductId(item.getProduct().getId().toString());
        checkoutItem.setThumbnailUrl(item.getProduct().getThumbnailUrl());
        if(item.getAttributes().isEmpty()){
            checkoutItem.setAttributes(null);
            checkoutItem.setSku(null);
            checkoutItem.setStock(item.getProduct().getQuantity());
        }
        else{
            checkoutItem.setAttributes(item.getAttributes());
            for(ProductSKU sku : item.getProduct().getSkuList()){
                if(new HashSet<>(sku.getAttributes()).containsAll(item.getAttributes())){
                    checkoutItem.setAttributes(sku.getAttributes());
                    checkoutItem.setSku(sku.getSku());
                    checkoutItem.setStock(sku.getQuantity());
                    break;
                }
            }
        }
        checkoutItem.setName(item.getProduct().getName());
        checkoutItem.setPrice(item.getPrice());
        return checkoutItem;
    }

    private int getItemStock(CartItem item){
        if(item.getProduct().getSkuList().isEmpty()) return item.getProduct().getQuantity();
        for(ProductSKU skU : item.getProduct().getSkuList()){
            if(new HashSet<>(skU.getAttributes()).containsAll(item.getAttributes())){
                return skU.getQuantity();
            }
        }
        return 0;
    }

}
