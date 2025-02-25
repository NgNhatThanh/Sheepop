package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.address.DistrictRepository;
import com.app.bdc_backend.dao.address.ProvinceRepository;
import com.app.bdc_backend.dao.address.WardRepository;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final ProvinceRepository provinceRepository;

    private final DistrictRepository districtRepository;

    private final WardRepository wardRepository;

    public List<Province> getProvinceList(){
        return provinceRepository.findAll();
    }

    public Province findProvinceByName(String name){
        return provinceRepository.findByName(name);
    }

    public List<District> findDistrictByName(String name){
        return districtRepository.findByName(name);
    }

    public List<Ward> findWardByName(String name){
        return wardRepository.findByName(name);
    }

    public List<District> findDistrictListByProvinceId(int provinceId){
        return districtRepository.findAllByProvinceId(provinceId);
    }

    public List<Ward> findWardListByDistrictId(int districtId){
        return wardRepository.findAllByDistrictId(districtId);
    }

}
