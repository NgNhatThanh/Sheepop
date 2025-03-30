package com.app.bdc_backend.schedule;

import com.app.bdc_backend.model.dto.BasicReviewInfo;
import com.app.bdc_backend.model.dto.response.ProductSaleInfo;
import com.app.bdc_backend.model.dto.response.ShopProductAndRevenue;
import com.app.bdc_backend.model.enums.CommonEntity;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.Order;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.Payment;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.ReviewService;
import com.app.bdc_backend.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScheduledService {

    private final ProductService productService;

    private final OrderService orderService;

    private final ShopService shopService;

    private final ReviewService reviewService;

//    @Scheduled(fixedDelay = 300000)
//    private void recalculateProductData(){
//        List<Product> products = productService.getAll();
//        for(Product product : products){
//            ProductSaleInfo saleInfo = orderService.getProductSaleInfo(product.getId());
//            BasicReviewInfo reviewInfo = reviewService.getProductReviewInfo(product.getId());
//            product.setRevenue(saleInfo.getRevenue());
//            product.setSold(saleInfo.getSold());
//            product.setAverageRating(reviewInfo.getAverageRating());
//            product.setTotalReviews(reviewInfo.getTotalReviews());
//            if(!product.getSkuList().isEmpty()){
//                long minPrice = product.getSkuList().get(0).getPrice();
//                int totalQuantity = 0;
//                for(ProductSKU sku : product.getSkuList()){
//                    minPrice = Math.min(minPrice, sku.getPrice());
//                    totalQuantity += sku.getQuantity();
//                }
//                product.setPrice(minPrice);
//                product.setQuantity(totalQuantity);
//            }
//        }
//        productService.saveAllProducts(products);
//        log.info("Recalculated product data");
//    }
//
//    @Scheduled(fixedDelay = 300000)
//    private void recalculateShopOrderData(){
//        List<ShopOrder> shopOrders = orderService.getAllShopOrder();
//        for(ShopOrder shopOrder : shopOrders){
//            long total = 0;
//            for(OrderItem item : shopOrder.getItems()) total += item.getPrice() * item.getQuantity();
//            total += shopOrder.getShippingFee();
//            shopOrder.setTotal(total);
//        }
//        orderService.saveAllShopOrders(shopOrders);
//        log.info("Recalculated shop order data");
//    }
//
//    @Scheduled(fixedDelay = 300000)
//    private void recalculateShopData(){
//        List<Shop> shops = shopService.getAll();
//        for(Shop shop : shops){
//            BasicReviewInfo reviewInfo = reviewService.getShopReviewInfo(shop.getId());
//            ShopProductAndRevenue productAndRevenue = productService.getShopProductAndRevenue(shop.getId());
//            shop.setTotalReviews(reviewInfo.getTotalReviews());
//            shop.setAverageRating(reviewInfo.getAverageRating());
//            shop.setProductCount(productAndRevenue.getProductCount());
//            shop.setRevenue(productAndRevenue.getRevenue());
//        }
//        shopService.saveAll(shops);
//        log.info("Recalculated shop data");
//    }

    @Scheduled(fixedDelay = 300000)
    private void cancelExpiredPayments(){
        List<Payment> pendingPayments = orderService.getAllPendingPayments();
        Date now = new Date();
        List<Payment> expiredPayments = new ArrayList<>();
        for(Payment payment: pendingPayments){
            if(now.after(payment.getExpireAt())){
                payment.setStatus(PaymentStatus.EXPIRED);
                payment.setUpdatedAt(new Date());
                expiredPayments.add(payment);
            }
        }
        List<Order> expiredOrders = orderService.getAllOrderByPayment(expiredPayments);
        List<ShopOrder> expiredShopOrder = new ArrayList<>();
        for(Order order: expiredOrders){
            List<ShopOrder> shopOrders = orderService.getAllShopOrderByOrder(order);
            for(ShopOrder shopOrder: shopOrders){
                shopOrder.setCanceledBy(CommonEntity.ADMIN);
                shopOrder.setCancelReason("Quá hạn thanh toán");
                shopOrder.setStatus(ShopOrderStatus.CANCELLED);
            }
            expiredShopOrder.addAll(shopOrders);
        }
        orderService.saveAllShopOrders(expiredShopOrder);
        orderService.saveAllPayments(expiredPayments);
    }

}
