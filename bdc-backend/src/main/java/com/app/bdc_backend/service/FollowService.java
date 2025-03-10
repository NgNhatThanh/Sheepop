package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public int getShopFollowCount(String shopId){
        return followRepository.countByShopId(shopId);
    }

}
