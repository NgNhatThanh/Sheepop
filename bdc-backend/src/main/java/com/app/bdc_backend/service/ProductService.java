package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.product.ProductAttributeRepository;
import com.app.bdc_backend.dao.product.ProductMediaRepository;
import com.app.bdc_backend.dao.product.ProductRepository;
import com.app.bdc_backend.dao.product.ProductSKURepository;
import com.app.bdc_backend.model.product.*;
import com.app.bdc_backend.model.shop.Shop;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    private ProductSKURepository productSkuRepository;

    private ProductMediaRepository productMediaRepository;

    private ProductAttributeRepository productAttributeRepository;

    public Page<Product> findAllForHomepage(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return productRepository.findAllByVisibleAndDeleted(true, false, pageable);
    }

    public Page<Product> findForShopProductTable(Shop shop, Pageable pageable) {
        return productRepository.findByShopAndDeleted(shop, false, pageable);
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

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void saveSKU(ProductSKU productSKU) {
        productSkuRepository.save(productSKU);
    }

    public Product findById(String id){
        return productRepository.findById(id).orElse(null);
    }

    public int countProductOfShop(ObjectId shopId){
        return productRepository.countByShopAndVisibleAndDeleted(shopId, true, false);
    }

    public void delete(Product product) {
        product.setVisible(false);
        product.setDeleted(true);
        saveProduct(product);
    }

    public int countByCategory(Category cat){
        return productRepository.countByCategory(cat);
    }

}
