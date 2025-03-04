package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.product.*;
import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.product.*;
import com.app.bdc_backend.model.shop.Shop;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<Product> getActiveProductsForShop(Shop shop,
                                                  String keyword,
                                                  Category category,
                                                  Pageable pageable) {
        if(category == null)
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndDeleted(
                    shop,
                    keyword,
                    false,
                    pageable);
        else
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndCategoryAndDeleted(
                    shop,
                    keyword,
                    category,
                    false,
                    pageable
            );
    }

    public Page<Product> getRestrictedProductForShop(Shop shop,
                                                     String keyword,
                                                     Category category,
                                                     Pageable pageable) {
        if(category == null)
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatus(
                    shop,
                    keyword,
                    true,
                    RestrictStatus.RESTRICTED,
                    pageable);
        else
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatusAndCategory(
                    shop,
                    keyword,
                    true,
                    RestrictStatus.RESTRICTED,
                    category,
                    pageable
            );
    }

    public Page<Product> getPendingRestrictProductsForShop(Shop shop,
                                                           String keyword,
                                                           Category category,
                                                           Pageable pageable){
        if(category == null)
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatus(
                    shop,
                    keyword,
                    true,
                    RestrictStatus.PENDING,
                    pageable);
        else
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatusAndCategory(
                    shop,
                    keyword,
                    true,
                    RestrictStatus.PENDING,
                    category,
                    pageable
            );
    }

    public Page<Product> getHiddenProductsForShop(Shop shop,
                                                  String keyword,
                                                  Category category,
                                                  Pageable pageable){
        if(category == null)
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndVisibleAndDeleted(
                    shop,
                    keyword,
                    false,
                    false,
                    pageable);
        else
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndVisibleAndDeletedAndCategory(
                    shop,
                    keyword,
                    false,
                    false,
                    category,
                    pageable
            );
    }

    public Page<Product> getOutOfStockProductsForShop(Shop shop,
                                                      String keyword,
                                                      Category category,
                                                      Pageable pageable){
        if(category == null)
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndDeletedAndQuantity(
                    shop,
                    keyword,
                    false,
                    0,
                    pageable);
        else
            return productRepository.findAllByShopAndNameContainingIgnoreCaseAndDeletedAndQuantityAndCategory(
                    shop,
                    keyword,
                    false,
                    0,
                    category,
                    pageable
            );
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

    public Page<Product> getActiveProductsForAdmin(String keyword,
                                                   int page,
                                                   int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return productRepository.findAllByDeletedAndNameContainingIgnoreCase(false, keyword, pageable);
    }

    public Page<Product> getRestrictedProductsForAdmin(String keyword,
                                                       int page,
                                                       int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return productRepository.findAllByRestrictedAndRestrictStatusAndNameContainingIgnoreCase(
                true,
                RestrictStatus.RESTRICTED,
                keyword,
                pageable);
    }

    public Page<Product> getPendingRestrictProductsForAdmin(String keyword,
                                                            int page,
                                                            int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return productRepository.findAllByRestrictedAndRestrictStatusAndNameContainingIgnoreCase(
                true,
                RestrictStatus.PENDING,
                keyword,
                pageable);
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public void saveAllProducts(List<Product> products){
        productRepository.saveAll(products);
    }

    public int countByCategory(Category cat){
        return productRepository.countByCategory(cat);
    }

}
