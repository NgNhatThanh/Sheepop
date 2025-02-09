package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.ProductAttributeRepository;
import com.app.bdc_backend.dao.ProductMediaRepository;
import com.app.bdc_backend.dao.ProductRepository;
import com.app.bdc_backend.dao.ProductSKURepository;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductAttribute;
import com.app.bdc_backend.model.product.ProductMedia;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    private ProductSKURepository productSkuRepository;

    private ProductMediaRepository productMediaRepository;

    private ProductAttributeRepository productAttributeRepository;

    public Page<Product> findAllForHomepage(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return productRepository.findAllByVisible(true, pageable);
    }

    public Page<Product> findByShop(Shop shop, Pageable pageable) {
        return productRepository.findByShop(shop, pageable);
    }

    public void addProductSKUList(List<ProductSKU> productSKU) {
        productSkuRepository.saveAll(productSKU);
    }

    public void addProductMediaList(List<ProductMedia> productMedia) {
        productMediaRepository.saveAll(productMedia);
    }

    public void addProductAttributeList(List<ProductAttribute> productAttribute) {
        productAttributeRepository.saveAll(productAttribute);
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public Product findById(String id){
        return productRepository.findById(id).orElse(null);
    }

    public int countProductOfShop(String shopId){
        return productRepository.countByShopId(new ObjectId(shopId));
    }

}
