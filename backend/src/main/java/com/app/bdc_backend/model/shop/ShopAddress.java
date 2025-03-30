package com.app.bdc_backend.model.shop;

import com.app.bdc_backend.model.address.Address;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shop_addresses")
@Getter
@Setter
public class ShopAddress extends Address {

    @Id
    private ObjectId id;

    private String shopId;

    private String phoneNumber;

    private String senderName;

}
