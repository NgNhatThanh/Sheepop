package com.app.bdc_backend.elasticsearch.service;

import com.app.bdc_backend.elasticsearch.dao.ESProductRepository;
import com.app.bdc_backend.elasticsearch.dao.ESProductRepository_I;
import com.app.bdc_backend.elasticsearch.model.ESProduct;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.exception.ServerException;
import com.app.bdc_backend.model.dto.request.ProductSearchFilters;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ESProductService {

    private final ESProductRepository productRepository;

    private final ESProductRepository_I productRepository_I;

    public Page<ProductCardDTO> homepageSearch(String keyword,
                                               String sortBy,
                                               String order,
                                               int page, int limit,
                                               ProductSearchFilters filters){
        switch (sortBy){
            case "relevance":
                sortBy = "_score";
                break;
            case "newest":
                sortBy = "createdAt";
                break;
            case "sales":
                sortBy = "sold";
                break;
            case "price":
                break;
            default:
                throw new RequestException("Invalid request: invalid sort by");
        }
        Sort sort = Sort.by(
                order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<ESProduct> pageRes;
        try {
            pageRes = productRepository.homepageSearch(keyword, pageable, filters);
        } catch (IOException e) {
            throw new ServerException("Internal Server Error: Elasticsearch");
        }
        return pageRes.map(es ->
                ModelMapper.getInstance().map(es, ProductCardDTO.class));
    }

    public Page<ProductCardDTO> getShopProductList(String shopId,
                                                   String categoryId,
                                                   String sortBy,
                                                   String order,
                                                   int page, int limit){
        switch (sortBy){
            case "popular":
                sortBy = "totalReviews";
                break;
            case "newest":
                sortBy = "createdAt";
                break;
            case "sales":
                sortBy = "sold";
                break;
            case "price":
                break;
            default:
                throw new RequestException("Invalid request: invalid sort by");
        }
        Sort sort = Sort.by(
                order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<ESProduct> pageRes;
        try {
            pageRes = productRepository.getShopProducts(shopId, categoryId, pageable);
        } catch (IOException e) {
            throw new ServerException("Internal Server Error: Elasticsearch");
        }
        return pageRes.map(es ->
                ModelMapper.getInstance().map(es, ProductCardDTO.class));
    }

    public int countRestrictedProductsOfShop(String shopId){
        return productRepository_I.countByShopIdAndRestricted(shopId, true);
    }

}
