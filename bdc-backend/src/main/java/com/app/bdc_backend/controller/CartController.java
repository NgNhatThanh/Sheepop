package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.dto.request.AddToCartDTO;
import com.app.bdc_backend.model.dto.response.CartMiniResponseDTO;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.CartService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.service.redis.impl.CartRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {

    private final UserService userService;

    private final ProductService productService;

    private final CartRedisService cartRedisService;

    private final CartService cartService;

    @GetMapping("/get-mini")
    public ResponseEntity<?> getCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Cart cart = cartRedisService.findByUser(user);
        if(cart == null){
           log.info("Missed cache - cart");
           cart = cartService.findByUser(user);
           cartRedisService.save(cart);
        }
        CartMiniResponseDTO dto = toCartMiniResponseDTO(cart);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Product product = productService.findById(dto.getProductId());
        if(product == null){
            log.warn("Add to cart error: Product not found");
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Product not found"
            ));
        }
        Cart cart = cartService.findByUser(user);
        try{
            cartService.addToCart(cart, product, dto.getQuantity(), dto.getAttributes());
            return ResponseEntity.ok().body(Map.of(
                    "msg", "Successfully added to cart"
            ));
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
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
