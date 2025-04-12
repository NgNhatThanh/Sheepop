package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/provinces")
    @Operation(summary = "Get list of all provinces")
    public ResponseEntity<List<Province>> getProvinceList(){
        return ResponseEntity.ok(addressService.getProvinceList());
    }

    @GetMapping("/districts")
    @Operation(summary = "Get list of districts by province ID")
    public ResponseEntity<List<District>> getDistrictList(@RequestParam int provinceId){
        List<District> res = addressService.findDistrictListByProvinceId(provinceId);
        if(res == null || res.isEmpty()){
            throw new RequestException("District not found");
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/wards")
    @Operation(summary = "Get list of wards by district ID")
    public ResponseEntity<List<Ward>> getWardList(@RequestParam int districtId){
        List<Ward> res = addressService.findWardListByDistrictId(districtId);
        if(res == null || res.isEmpty()){
            throw new RequestException("Ward not found");
        }
        return ResponseEntity.ok(res);
    }

}
