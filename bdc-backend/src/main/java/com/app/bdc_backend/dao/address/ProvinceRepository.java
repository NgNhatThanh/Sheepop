package com.app.bdc_backend.dao.address;

import com.app.bdc_backend.model.address.Province;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends MongoRepository<Province, Integer> {

    Province findByName(String name);

}
