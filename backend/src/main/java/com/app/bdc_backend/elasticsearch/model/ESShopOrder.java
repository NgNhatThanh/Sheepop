package com.app.bdc_backend.elasticsearch.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "shop_orders")
@Getter
@Setter
public class ESShopOrder {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String shopId;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private String orderId;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Long)
    private long total;

    @Field(type = FieldType.Date)
    private Date createdAt;

}
