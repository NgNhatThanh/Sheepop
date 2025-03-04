package com.app.bdc_backend.facade;

import com.app.bdc_backend.model.dto.response.ProductSaleInfo;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScheduledService {

    private final ProductService productService;

    private final OrderService orderService;

    @Scheduled(fixedDelay = 300000)
    private void recalculateProductData(){
        List<Product> products = productService.getAll();
        for(Product product : products){
            ProductSaleInfo saleInfo = orderService.getProductSaleInfo(product.getId());
            product.setRevenue(saleInfo.getRevenue());
            product.setSold(saleInfo.getSold());
            if(!product.getSkuList().isEmpty()){
                long minPrice = product.getSkuList().get(0).getPrice();
                int totalQuantity = 0;
                for(ProductSKU sku : product.getSkuList()){
                    minPrice = Math.min(minPrice, sku.getPrice());
                    totalQuantity += sku.getQuantity();
                }
                product.setPrice(minPrice);
                product.setQuantity(totalQuantity);
            }
        }
        productService.saveAllProducts(products);
        log.info("Recalculated product data");
    }

}
