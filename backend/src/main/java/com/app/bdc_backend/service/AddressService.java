package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.address.DistrictRepository;
import com.app.bdc_backend.dao.address.ProvinceRepository;
import com.app.bdc_backend.dao.address.WardRepository;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.Address;
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

    public Address setInfo(Address address, String provinceName, String districtName, String wardName){
        Province province = findProvinceByName(provinceName);
        if(province == null){
            throw new RequestException("Invalid address: province");
        }
        address.setProvince(province);
        List<District> districts = findDistrictByName(districtName);
        boolean okDistrict = false;
        for(District d : districts){
            if(d.getProvinceId() == province.getId()){
                okDistrict = true;
                address.setDistrict(d);
                break;
            }
        }
        if(!okDistrict){
            throw new RequestException("Invalid address: district");
        }
        List<Ward> ward = findWardByName(wardName);
        boolean okWard = false;
        for(Ward w : ward){
            if(w.getDistrictId() == address.getDistrict().getId()){
                okWard = true;
                address.setWard(w);
                break;
            }
        }
        if(!okWard){
            throw new RequestException("Invalid address: ward");
        }
        return address;
    }

    public List<District> findDistrictListByProvinceId(int provinceId){
        return districtRepository.findAllByProvinceId(provinceId);
    }

    public List<Ward> findWardListByDistrictId(int districtId){
        return wardRepository.findAllByDistrictId(districtId);
    }

}
