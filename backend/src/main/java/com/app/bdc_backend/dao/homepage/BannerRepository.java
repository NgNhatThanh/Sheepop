package com.app.bdc_backend.dao.homepage;

import com.app.bdc_backend.model.homepage.Banner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends MongoRepository<Banner,String> {

    Optional<List<Banner>> findAllByIdNotNull();

}
