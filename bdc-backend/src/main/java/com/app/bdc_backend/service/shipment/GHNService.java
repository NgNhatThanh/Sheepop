package com.app.bdc_backend.service.shipment;

import com.app.bdc_backend.model.BasicShippingOrderInfo;
import com.app.bdc_backend.model.ShipmentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class GHNService implements IShipmentService{

    @Value("${GHN.SHOPID}")
    private int shopId;

    @Value("${GHN.TOKEN}")
    private String token;

    private String getServiceApi = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";

    private String calculateFeeApi = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

    private String getExpectedDeliveryDateApi = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/leadtime";

    private String getProvinceListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";

    private String getDistrictListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";

    private String getWardListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public BasicShippingOrderInfo calculateShipmentFee(ShipmentInfo info) throws Exception{
        BasicShippingOrderInfo orderInfo = new BasicShippingOrderInfo();

        int fromProvinceId = getProvinceId(info.getFrom().getProvince().getName());
        int fromDistricId = getDistrictId(fromProvinceId, info.getFrom().getDistrict().getName());
        String fromWardCode = getWardCode(fromDistricId, info.getFrom().getWard().getName());
        int toProvinceId = getProvinceId(info.getTo().getProvince().getName());
        int toDistricId = getDistrictId(toProvinceId, info.getTo().getDistrict().getName());
        String toWardCode = getWardCode(toDistricId, info.getTo().getWard().getName());
        List<Integer> serviceIds = getServiceIdList(fromDistricId, toDistricId);
        int fee = getFee(info.getWeight() <= 20000 ? serviceIds.get(0) : serviceIds.get(1),
                fromDistricId, toDistricId,
                fromWardCode, toWardCode, info.getWeight());
        Date expDeliveryTime = getExpectedDeliveryDate(serviceIds.get(0), fromDistricId, toDistricId,
                fromWardCode, toWardCode, info.getWeight());

        orderInfo.setFee(fee);
        orderInfo.setExpectedDeliveryDate(expDeliveryTime);
        return orderInfo;
    }

    private int getFee(int serviceId, int fromDistricId, int toDistricId,
                       String fromWardCode, String toWardCode, int weight) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", token);
        headers.add("ShopId", String.valueOf(shopId));
        Map<String, Object> body = new HashMap<>();
        body.put("service_id" , serviceId);
        body.put("from_district_id" , fromDistricId);
        body.put("to_district_id" , toDistricId);
        body.put("from_ward_code" , fromWardCode);
        body.put("to_ward_code" , toWardCode);
        body.put("weight", weight);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(calculateFeeApi,
                requestEntity,
                Map.class);
        if(response.getStatusCode() == HttpStatus.OK){
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("data");
            return (int)responseBody.get("total");
        }
        else throw new Exception("Invalid information");
    }

    private Date getExpectedDeliveryDate(int serviceId, int fromDistricId, int toDistricId,
                                         String fromWardCode, String toWardCode, int weight) throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", token);
        headers.add("ShopId", String.valueOf(shopId));
        Map<String, Object> body = new HashMap<>();
        body.put("service_id" , serviceId);
        body.put("from_district_id" , fromDistricId);
        body.put("to_district_id" , toDistricId);
        body.put("from_ward_code" , fromWardCode);
        body.put("to_ward_code" , toWardCode);
        body.put("weight", weight);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(getExpectedDeliveryDateApi,
                requestEntity,
                Map.class);
        if(response.getStatusCode() == HttpStatus.OK){
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("data");
            int timestamp = (int)responseBody.get("leadtime");
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("Asia/Bangkok"));
            return Date.from(Instant.ofEpochSecond(timestamp));
        }
        else throw new Exception("Invalid information");
    }

    private int getProvinceId(String province) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(getProvinceListApi,
                HttpMethod.GET,
                requestEntity,
                Map.class);
        if(response.getStatusCode() == HttpStatus.OK){
            List<Map<String, Object>> data =  (List<Map<String, Object>>) response.getBody().get("data");
            for(Map<String, Object> map : data){
                List<String> extensionNames = (List<String>)map.get("NameExtension");
                if(extensionNames == null || extensionNames.isEmpty()){
                    if(map.get("ProvinceName").equals(province)){
                        return Integer.parseInt((String)map.get("ProvinceId"));
                    }
                }
                else if(extensionNames.contains(province)){
                    return (int)map.get("ProvinceID");
                }
            }
        }
        return -1;
    }

    private int getDistrictId(int provinceId, String district) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Integer> body = new HashMap<>();
        body.put("province_id", provinceId);
        HttpEntity<Map<String, Integer>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(getDistrictListApi,
                requestEntity,
                Map.class);
        if(response.getStatusCode() == HttpStatus.OK){
            List<Map<String, Object>> data =  (List<Map<String, Object>>) response.getBody().get("data");
            for(Map<String, Object> map : data){
                List<String> extensionNames = (List<String>)map.get("NameExtension");
                if(extensionNames == null || extensionNames.isEmpty()){
                    if(map.get("DistrictName").equals(district)){
                        return Integer.parseInt((String)map.get("DistrictID"));
                    }
                }
                if(extensionNames.contains(district)){
                    return (int)map.get("DistrictID");
                }
            }
        }
        return -1;
    }

    private String getWardCode(int districtId, String ward) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Integer> body = new HashMap<>();
        body.put("district_id", districtId);
        HttpEntity<Map<String, Integer>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(getWardListApi,
                requestEntity,
                Map.class);
        if(response.getStatusCode() == HttpStatus.OK){
            List<Map<String, Object>> data =  (List<Map<String, Object>>) response.getBody().get("data");
            for(Map<String, Object> map : data){
                List<String> extensionNames = (List<String>)map.get("NameExtension");
                if(extensionNames == null || extensionNames.isEmpty()){
                    if(map.get("WardName").equals(ward)){
                        return (String)map.get("WardCode");
                    }
                }
                if(extensionNames.contains(ward)){
                    return (String)map.get("WardCode");
                }
            }
        }
        return null;
    }

    private List<Integer> getServiceIdList(int fromDistrict, int toDistrict) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", token);

        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", shopId);
        body.put("from_district", fromDistrict);
        body.put("to_district", toDistrict);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(getServiceApi,
                requestEntity,
                Map.class);

        List<Integer> res = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseBody.get("data");
            for(Map<String, Object> data : dataList){
                res.add((int)data.get("service_id"));
            }
        }
        return res;
    }

}
