package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.shop.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    int countByShopAndVisibleAndDeleted(ObjectId shopId, boolean visible, boolean deleted);

    Page<Product> findByShopAndDeleted(Shop shop, boolean deleted, Pageable pageable);

    Page<Product> findAllByVisibleAndDeleted(boolean visible, boolean deleted, Pageable pageable);

    int countByCategory(Category category);

    Page<Product> findAllByDeletedAndNameContainingIgnoreCase(boolean deleted, String keyword, Pageable pageable);

    Page<Product> findAllByRestrictedAndRestrictStatusAndNameContainingIgnoreCase(boolean restrict,
                                                                                RestrictStatus restrictStatus,
                                                                                String name,
                                                                                Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndDeleted(Shop shop,
                                                                     String name,
                                                                     boolean deleted,
                                                                     Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndCategoryAndDeleted(Shop shop,
                                                                                String name,
                                                                                Category category,
                                                                                boolean deleted,
                                                                                Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatus(Shop shop,
                                                                                         String name,
                                                                                         boolean restricted,
                                                                                         RestrictStatus status,
                                                                                         Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndRestrictedAndRestrictStatusAndCategory(Shop shop,
                                                                                         String name,
                                                                                         boolean restricted,
                                                                                         RestrictStatus status,
                                                                                         Category category,
                                                                                         Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndVisibleAndDeleted(Shop shop,
                                                                               String name,
                                                                               boolean visible,
                                                                               boolean deleted,
                                                                               Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndVisibleAndDeletedAndCategory(Shop shop,
                                                                                          String name,
                                                                                          boolean visible,
                                                                                          boolean deleted,
                                                                                          Category category,
                                                                                          Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndDeletedAndQuantity(Shop shop,
                                                                                String name,
                                                                                boolean deleted,
                                                                                int quantity,
                                                                                Pageable pageable);

    Page<Product> findAllByShopAndNameContainingIgnoreCaseAndDeletedAndQuantityAndCategory(Shop shop,
                                                                                           String name,
                                                                                           boolean deleted,
                                                                                           int quantity,
                                                                                           Category category,
                                                                                           Pageable pageable);

}
