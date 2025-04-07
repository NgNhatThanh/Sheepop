package com.app.bdc_backend.controller;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinceList(){
        return ResponseEntity.ok(addressService.getProvinceList());
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getDistrictList(@RequestParam int provinceId){
        List<District> res = addressService.findDistrictListByProvinceId(provinceId);
        if(res == null || res.isEmpty()){
            throw new RequestException("District not found");
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWardList(@RequestParam int districtId){
        List<Ward> res = addressService.findWardListByDistrictId(districtId);
        if(res == null || res.isEmpty()){
            throw new RequestException("Ward not found");
        }
        return ResponseEntity.ok(res);
    }

}
