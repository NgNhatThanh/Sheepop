package com.app.bdc_backend.controller;

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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWardList(@RequestParam int districtId){
        List<Ward> res = addressService.findWardListByDistrictId(districtId);
        if(res == null || res.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

}
