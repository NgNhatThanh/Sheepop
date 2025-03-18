package com.app.bdc_backend.elasticsearch.service;

import com.app.bdc_backend.elasticsearch.dao.ESShopOrderRepository;
import com.app.bdc_backend.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ESShopOrderService {

    private final ESShopOrderRepository esShopOrderRepository;

    public Map<Integer, Integer> getShopOrderStatusCount(String shopId){
        try {
            return esShopOrderRepository.countShopOrdersByStatus(shopId);
        } catch (IOException e) {
            throw new ServerException("Internal Server: ES");
        }
    }

}
