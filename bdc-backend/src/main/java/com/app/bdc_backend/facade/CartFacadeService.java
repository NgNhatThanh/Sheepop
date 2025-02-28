package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.dto.request.AddToCartDTO;
import com.app.bdc_backend.model.dto.request.CartItemUpdateDTO;
import com.app.bdc_backend.model.dto.response.CartDTO;
import com.app.bdc_backend.model.dto.response.CartMiniResponseDTO;
import com.app.bdc_backend.model.dto.response.CartUpdateResponseDTO;
import com.app.bdc_backend.model.dto.response.ShopCartDTO;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.CartService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.service.redis.impl.CartRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartFacadeService {

    private final UserService userService;

    private final ProductService productService;

    private final CartRedisService cartRedisService;

    private final CartService cartService;

    public CartMiniResponseDTO getMiniCart(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            User user = userService.findByUsername(username);
            log.info("Get mini cart: missed cache");
            cart = cartService.findByUser(user);
            cartRedisService.save(cart);
        }
        return toCartMiniResponseDTO(cart);
    }

    public void addToCart(AddToCartDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productService.findById(dto.getProductId());
        if(product == null || !product.isVisible()){
            log.warn("Add to cart error: Product not found");
            throw new RequestException("Product not found");
        }
        if(product.getShop().getUser().getUsername().equals(username)){
            throw new RequestException("Invalid request: cannot buy your own product");
        }
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            User user = userService.findByUsername(username);
            log.info("Add to cart: missed cache");
            cart = cartService.findByUser(user);
        }
        try{
            cart = cartService.addToCart(cart, product, dto.getQuantity(), dto.getAttributes());
        }
        catch (RequestException e){
            throw new RequestException("Failed to add to cart: " + e.getMessage());
        }
        finally {
            cartRedisService.save(cart);
        }
    }

    public CartDTO getCartItems(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            User user = userService.findByUsername(username);
            cart = cartService.findByUser(user);
        }
        else{
            List<CartItem> uptList = new ArrayList<>();
            for(CartItem item : cart.getItems()){
                if(!item.getProduct().isVisible()){
                    cart.getItems().remove(item);
                    continue;
                }
                item.setProduct(productService.findById(item.getProduct().getId().toString()));
                if(item.getQuantity() > getItemStock(item)){
                    item.setSelected(false);
                    uptList.add(item);
                }
            }
            cartService.saveAllItem(uptList);
        }
        cartRedisService.save(cart);
        return toCartDTO(cart);
    }

    public CartUpdateResponseDTO updateCart(List<CartItemUpdateDTO> dtoList){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRedisService.findByUser(username);
        if(cart == null){
            User user = userService.findByUsername(username);
            cart = cartService.findByUser(user);
        }
        else{
            for(CartItem item : cart.getItems()){
                item.setProduct(productService.findById(item.getProduct().getId().toString()));
            }
        }
        List<CartItem> uptList = new ArrayList<>();
        CartUpdateResponseDTO resDTO = new CartUpdateResponseDTO();
        for(CartItem item : cart.getItems()){
            for(CartItemUpdateDTO dto : dtoList){
                if(dto.getItemId().equals(item.getId().toString())){
                    item.setSelected(dto.isSelected());
                    int stock = getItemStock(item);
                    if(dto.getQuantity() > stock){
                        item.setSelected(false);
                        resDTO.setWarnMsg("Số lượng vượt quá số lượng còn lại: " + stock);
                    }
                    item.setQuantity(stock > 0 ?
                            Math.min(stock, dto.getQuantity()) : 1);
                    uptList.add(item);
                    break;
                }
            }
        }
        cartService.saveAllItem(uptList);
        cartRedisService.save(cart);
        resDTO.setCart(toCartDTO(cart));
        return resDTO;
    }

    public CartDTO removeItem(String itemId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRedisService.findByUser(username);
        boolean hitCache = true;
        if(cart == null){
            hitCache = false;
            User user = userService.findByUsername(username);
            cart = cartService.findByUser(user);
        }
        for(CartItem item : cart.getItems()){
            if(item.getId().toString().equals(itemId)){
                cart.getItems().remove(item);
                cartService.deleteItem(item);
                break;
            }
        }
        if(hitCache){
            for(CartItem item : cart.getItems()){
                item.setProduct(productService.findById(item.getProduct().getId().toString()));
            }
        }
        cartRedisService.save(cart);
        cartService.save(cart);
        return toCartDTO(cart);
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

    private CartDTO toCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        Map<String, ShopCartDTO> mp = new HashMap<>();
        for(CartItem item : cart.getItems()){
            mp.putIfAbsent(item.getProduct().getShop().getId().toString(), new ShopCartDTO());
            ShopCartDTO shopCartDTO = mp.get(item.getProduct().getShop().getId().toString());
            if(shopCartDTO.getShop() == null){
                ShopCartDTO.ShopDTO shopDTO = new ShopCartDTO.ShopDTO();
                shopDTO.setId(item.getProduct().getShop().getId().toString());
                shopDTO.setName(item.getProduct().getShop().getName());
                shopDTO.setUsername(item.getProduct().getShop().getUser().getUsername());
                shopCartDTO.setShop(shopDTO);
            }
            ShopCartDTO.ShopCartItem cartItem = new ShopCartDTO.ShopCartItem();
            cartItem.setItemId(item.getId().toString());
            cartItem.setQuantity(item.getQuantity());
            cartItem.setSelected(item.isSelected());
            cartItem.setProductId(item.getProduct().getId().toString());
            cartItem.setThumbnailUrl(item.getProduct().getThumbnailUrl());
            if(item.getAttributes().isEmpty()){
                cartItem.setAttributes(null);
                cartItem.setSku(null);
                cartItem.setStock(item.getProduct().getQuantity());
            }
            else{
                cartItem.setAttributes(item.getAttributes());
                for(ProductSKU sku : item.getProduct().getSkuList()){
                    if(new HashSet<>(sku.getAttributes()).containsAll(item.getAttributes())){
                        cartItem.setAttributes(sku.getAttributes());
                        cartItem.setSku(sku.getSku());
                        cartItem.setStock(sku.getQuantity());
                        break;
                    }
                }
            }
            cartItem.setName(item.getProduct().getName());
            cartItem.setPrice(item.getPrice());
            shopCartDTO.getItems().add(cartItem);
            mp.put(item.getProduct().getShop().getId().toString(), shopCartDTO);
        }
        dto.setShopCarts(mp.values().stream().toList());
        return dto;
    }

    private CartMiniResponseDTO toCartMiniResponseDTO(Cart cart) {
        CartMiniResponseDTO dto = new CartMiniResponseDTO();
        dto.setUpdatedAt(cart.getUpdatedAt());
        List<CartMiniResponseDTO.MiniCartItem> miniItems = new ArrayList<>();
        for(CartItem item : cart.getItems()){
            CartMiniResponseDTO.MiniCartItem miniItem = new CartMiniResponseDTO.MiniCartItem();
            miniItem.setPrice(item.getPrice());
            miniItem.setQuantity(item.getQuantity());
            miniItem.setName(item.getProduct().getName());
            miniItem.setThumbnailUrl(item.getProduct().getThumbnailUrl());
            miniItem.setAttributes(item.getAttributes());
            miniItems.add(miniItem);
        }
        dto.setItems(miniItems);
        return dto;
    }

}
