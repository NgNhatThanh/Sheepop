package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.cart.CartItemRepository;
import com.app.bdc_backend.dao.cart.CartRepository;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductAttribute;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    public Cart findByUser(User user){
        return cartRepository.findByUser(user);
    }

    public Cart save(Cart cart){
        return cartRepository.save(cart);
    }

    public Cart addToCart(Cart cart, Product product, int quantity, List<ProductAttribute> attributes){
        if(product.getSkuList().isEmpty() && quantity > product.getQuantity())
            throw new RequestException("Không thể thêm sản phẩm do số lượng vượt quá số lượng còn lại: " + product.getQuantity());
        for(ProductSKU sku : product.getSkuList()){
            if(new HashSet<>(sku.getAttributes()).containsAll(attributes)){
                if(quantity > sku.getQuantity())
                    throw new RequestException("Không thể thêm sản phẩm do số lượng vượt quá số lượng còn lại: " + sku.getQuantity());
            }
        }
        for(CartItem it : cart.getItems()){
            if(it.getProduct().equals(product) && new HashSet<>(it.getAttributes()).containsAll(attributes)){
                if(product.getSkuList().isEmpty() && it.getQuantity() + quantity > product.getQuantity())
                    throw new RequestException("Không thể thêm sản phẩm do tổng số lượng vượt quá số lượng còn lại: " + product.getQuantity());
                for(ProductSKU sku : product.getSkuList()){
                    if(new HashSet<>(sku.getAttributes()).containsAll(attributes)){
                        if(it.getQuantity() + quantity > sku.getQuantity())
                            throw new RequestException("Không thể thêm sản phẩm do tổng số lượng vượt quá số lượng còn lại: " + sku.getQuantity());
                    }
                }
                it.setQuantity(it.getQuantity() + quantity);
                cartItemRepository.save(it);
                return save(cart);
            }
        }
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setCart(cart);
        item.setAttributes(attributes);
        item.setQuantity(quantity);

        if(product.getSkuList().isEmpty()) item.setPrice(product.getPrice());
        else{
            for(ProductSKU sku : product.getSkuList()){
                if(new HashSet<>(sku.getAttributes()).containsAll(attributes)){
                    item.setPrice(sku.getPrice());
                    break;
                }
            }
        }

        cartItemRepository.save(item);
        cart.getItems().add(item);
        cart.setUpdatedAt(new Date());
        return save(cart);
    }

    public void saveAllItem(List<CartItem> items){
        cartItemRepository.saveAll(items);
    }

    public void deleteItem(CartItem item){
        cartItemRepository.delete(item);
    }

    public int getItemStock(CartItem item){
        if(item.getProduct().getSkuList().isEmpty()) return item.getProduct().getQuantity();
        for(ProductSKU skU : item.getProduct().getSkuList()){
            if(new HashSet<>(skU.getAttributes()).containsAll(item.getAttributes())){
                return skU.getQuantity();
            }
        }
        return 0;
    }

}
