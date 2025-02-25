package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.model.dto.request.AddProductDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductSKUResponseDTO;
import com.app.bdc_backend.model.dto.response.ShopProductTableResponseDTO;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/product")
public class ShopProductController {

    private final UserService userService;

    private final ShopService shopService;

    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody AddProductDTO productDTO) {
        try{
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);
            Shop shop = shopService.findByUser(user);
            Product product = ModelMapper.getInstance().map(productDTO, Product.class);
            product.setShop(shop);
            product.setCreatedAt(new Date());
            Category category = ModelMapper.getInstance().map(productDTO.getCategory(), Category.class);
            product.setCategory(category);

            for(ProductSKU sku : product.getSkuList()){
                sku.setProduct(product);
                productService.addProductAttributeList(sku.getAttributes());
            }
            productService.addProductMediaList(product.getMediaList());
            productService.addProductSKUList(product.getSkuList());
            productService.saveProduct(product);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ShopProductTableResponseDTO>> getProductList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit);
        Page<Product> productList = productService.findByShop(shop, pageable);
        PageResponse<ShopProductTableResponseDTO> response = new PageResponse<>(
                productList.map(this::toProductTableDTO)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change_visible")
    public ResponseEntity<?> changeVisible(@RequestParam String productId){
        Product product = productService.findById(productId);
        if(product == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request: Product not found"
            ));
        }
        product.setVisible(!product.isVisible());
        productService.saveProduct(product);
        return ResponseEntity.ok().build();
    }

    private ShopProductTableResponseDTO toProductTableDTO(Product product) {
        ShopProductTableResponseDTO dto = new ShopProductTableResponseDTO();
        dto.setId(product.getId().toString());
        dto.setName(product.getName());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setVisible(product.isVisible());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        if(!product.getSkuList().isEmpty()){
            for(ProductSKU sku : product.getSkuList()){
                dto.setQuantity(dto.getQuantity() + sku.getQuantity());
                dto.getSkuList().add(ModelMapper.getInstance().map(sku, ProductSKUResponseDTO.class));
            }
        }
        else{
            dto.setQuantity(product.getQuantity());
            dto.setPrice(product.getPrice());
        }
        return dto;
    }

}
