package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.CartFacadeService;
import com.app.bdc_backend.model.dto.request.AddToCartDTO;
import com.app.bdc_backend.model.dto.request.CartItemUpdateDTO;
import com.app.bdc_backend.model.dto.response.CartDTO;
import com.app.bdc_backend.model.dto.response.CartMiniResponseDTO;
import com.app.bdc_backend.model.dto.response.CartUpdateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class CartController {

    private final CartFacadeService cartFacadeService;

    @GetMapping("/get-mini")
    @Operation(summary = "Get mini cart information")
    public ResponseEntity<CartMiniResponseDTO> getCart() {
        return ResponseEntity.ok(cartFacadeService.getMiniCart());
    }

    @PostMapping("/add-to-cart")
    @Operation(summary = "Add product to cart")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody @Valid AddToCartDTO dto) {
        cartFacadeService.addToCart(dto);
        return ResponseEntity.ok().body(Map.of(
                "msg", "Successfully added to cart"
        ));
    }

    @GetMapping("/get")
    @Operation(summary = "Get full cart information")
    public ResponseEntity<CartDTO> getCartItems() {
        return ResponseEntity.ok(cartFacadeService.getCartItems());
    }

    @PostMapping("/update")
    @Operation(summary = "Update cart items")
    public ResponseEntity<CartUpdateResponseDTO> updateCart(@RequestBody @NotEmpty List<@Valid CartItemUpdateDTO> dtoList) {
        return ResponseEntity.ok(cartFacadeService.updateCart(dtoList));
    }

    @PostMapping("/item/remove")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartDTO> removeItem(@RequestParam(value = "itemId") String itemId) {
        return ResponseEntity.ok(cartFacadeService.removeItem(itemId));
    }
}