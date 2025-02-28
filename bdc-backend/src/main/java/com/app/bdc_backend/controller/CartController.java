package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.CartFacadeService;
import com.app.bdc_backend.model.dto.request.AddToCartDTO;
import com.app.bdc_backend.model.dto.request.CartItemUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {

    private final CartFacadeService cartFacadeService;

    @GetMapping("/get-mini")
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok(cartFacadeService.getMiniCart());
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartDTO dto) {
        cartFacadeService.addToCart(dto);
        return ResponseEntity.ok().body(Map.of(
                "msg", "Successfully added to cart"
        ));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCartItems() {
        return ResponseEntity.ok(cartFacadeService.getCartItems());
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody List<CartItemUpdateDTO> dtoList) {
        return ResponseEntity.ok(cartFacadeService.updateCart(dtoList));
    }

    @PostMapping("/item/remove")
    public ResponseEntity<?> removeItem(@RequestParam(value = "itemId") String itemId) {
        return ResponseEntity.ok(cartFacadeService.removeItem(itemId));
    }

}
