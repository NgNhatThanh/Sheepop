package com.app.bdc_backend.model.shop;

import com.app.bdc_backend.model.address.Address;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shop_addresses")
@Getter
@Setter
public class ShopAddress extends Address {

    private ObjectId id;

    private String shopId;

}
