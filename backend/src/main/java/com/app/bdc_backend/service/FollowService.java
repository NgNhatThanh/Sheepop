package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.FollowRepository;
import com.app.bdc_backend.model.shop.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void save(Follow follow) {
        followRepository.save(follow);
    }

    @Transactional
    public void delete(Follow follow) {
        followRepository.delete(follow);
    }

    public Follow find(String shopId, String userId){
        return followRepository.findByShopIdAndUserId(shopId, userId);
    }

}
