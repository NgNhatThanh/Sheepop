package com.app.bdc_backend.elasticsearch.dao;

import com.app.bdc_backend.elasticsearch.model.ESProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESProductRepository_I extends ElasticsearchRepository<ESProduct, String> {

    int countByShopIdAndRestricted(String shopId, boolean restricted);

}
