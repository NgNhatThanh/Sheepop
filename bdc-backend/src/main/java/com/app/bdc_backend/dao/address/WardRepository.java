package com.app.bdc_backend.dao.address;

import com.app.bdc_backend.model.address.Ward;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends MongoRepository<Ward, Integer> {

    List<Ward> findByName(String name);

    List<Ward> findAllByDistrictId(int districtId);

}
