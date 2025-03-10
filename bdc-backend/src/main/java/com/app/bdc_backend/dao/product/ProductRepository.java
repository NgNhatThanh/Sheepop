package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.dto.ProductPageImpl;
import com.app.bdc_backend.model.dto.response.MyPageImpl;
import com.app.bdc_backend.model.dto.response.ShopProductAndRevenue;
import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.shop.Shop;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    int countByShopAndVisibleAndDeleted(ObjectId shopId, boolean visible, boolean deleted);

    Page<Product> findByShopAndDeleted(Shop shop, boolean deleted, Pageable pageable);

    Page<Product> findAllByVisibleAndDeleted(boolean visible, boolean deleted, Pageable pageable);

    int countByCategory(Category category);

    @Aggregation(pipeline = {
        "{ $lookup: { from: 'shops', localField: 'shop', foreignField: '_id', as: 'shop_ori' }}",
        "{ $match: { 'shop_ori.name': { $regex: ?2, $options: 'i' }, 'deleted': ?0, 'name': { $regex: ?1, $options: 'i' }} }",
        "{ $sort: { ?5: ?6 } }",
        "{ $group: { _id: null, totalElements: { $sum: 1 }, content: { $push: '$$ROOT' }}}",
        "{ $project: { _id: 0, totalElements: 1, content: { $slice: ['$content', ?3, ?4] } }}"
    })
    ProductPageImpl findActiveProductsForAdmin(boolean deleted,
                                              String productName,
                                              String shopName,
                                              long offset,
                                              int limit,
                                              String sortBy,
                                              int direction);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'shops', localField: 'shop', foreignField: '_id', as: 'shop_ori' }}",
            "{ $match: { 'shop_ori.name': { $regex: ?3, $options: 'i' }, 'deleted': ?0, 'restrictStatus': ?1, 'name': { $regex: ?2, $options: 'i' }} }",
            "{ $sort: { ?6: ?7 } }",
            "{ $group: { _id: null, totalElements: { $sum: 1 }, content: { $push: '$$ROOT' }}}",
            "{ $project: { _id: 0, totalElements: 1, content: { $slice: ['$content', ?4, ?5] } }}"
    })
    ProductPageImpl findRestrictedProductsForAdminByStatus(boolean restrict,
                                                           RestrictStatus restrictStatus,
                                                           String productName,
                                                           String shopName,
                                                           long offset,
                                                           int limit,
                                                           String sortBy,
                                                           int direction);

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

    @Aggregation(pipeline = {
        "{ $match: { shop: ?0 } }",
        "{ $group: { _id: null, productCount: { $sum: 1 }, revenue: { $sum: '$revenue' } } }"
    })
    ShopProductAndRevenue findShopProductCountAndRevenue(ObjectId shopId);

    List<Product> findAllByShop(Shop shop);
}
