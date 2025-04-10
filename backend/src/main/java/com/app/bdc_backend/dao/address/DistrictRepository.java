package com.app.bdc_backend.dao.address;

import com.app.bdc_backend.model.address.District;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends MongoRepository<District, Integer> {

    List<District> findByName(String name);

    List<District> findAllByProvinceId(int provinceId);

}
