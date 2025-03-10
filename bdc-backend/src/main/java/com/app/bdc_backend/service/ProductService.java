package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.product.*;
import com.app.bdc_backend.model.dto.ProductPageImpl;
import com.app.bdc_backend.model.dto.response.MyPageImpl;
import com.app.bdc_backend.model.dto.response.ShopProductAndRevenue;
import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.product.*;
import com.app.bdc_backend.model.shop.Shop;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    private ProductSKURepository productSkuRepository;

    private ProductMediaRepository productMediaRepository;

    private ProductAttributeRepository productAttributeRepository;

    public List<Product> getAllByShop(Shop shop){
        return productRepository.findAllByShop(shop);
    }

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

    public Page<Product> getActiveProductsForAdmin(String productName,
                                                   String shopName,
                                                   int page,
                                                   int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        ProductPageImpl res = productRepository.findActiveProductsForAdmin(
                false,
                productName,
                shopName,
                pageable.getOffset(),
                limit,
                "updatedAt", -1);
        if(res == null)
            return new PageImpl<>(new ArrayList<>());
        return new PageImpl<>(res.getContent(), pageable, res.getTotalElements());
    }

    public Page<Product> getRestrictedProductsForAdmin(String productName,
                                                       String shopName,
                                                       int page,
                                                       int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        ProductPageImpl res = productRepository.findRestrictedProductsForAdminByStatus(
                true,
                RestrictStatus.RESTRICTED,
                productName,
                shopName,
                pageable.getOffset(),
                limit,
                "updatedAt", -1);
        if(res == null)
            return new PageImpl<>(new ArrayList<>());
        return new PageImpl<>(res.getContent(), pageable, res.getTotalElements());
    }

    public Page<Product> getPendingRestrictProductsForAdmin(String productName,
                                                            String shopName,
                                                            int page,
                                                            int limit){
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        ProductPageImpl res = productRepository.findRestrictedProductsForAdminByStatus(
                true,
                RestrictStatus.PENDING,
                productName,
                shopName,
                pageable.getOffset(),
                limit,
                "updatedAt", -1);
        if(res == null)
            return new PageImpl<>(new ArrayList<>());
        return new PageImpl<>(res.getContent(), pageable, res.getTotalElements());
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

    public ShopProductAndRevenue getShopProductAndRevenue(ObjectId shopId){
        ShopProductAndRevenue productAndRevenue = productRepository.findShopProductCountAndRevenue(shopId);
        if(productAndRevenue == null)
            return new ShopProductAndRevenue();
        return productAndRevenue;
    }

}
