package com.app.bdc_backend.elasticsearch.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "products")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ESProduct {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String thumbnailUrl;

    @Field(type = FieldType.Text)
    private String categoryId;

    @Field(type = FieldType.Long)
    private long price;

    @Field(type = FieldType.Integer)
    private int sold;

    @Field(type = FieldType.Double)
    private double averageRating;

    @Field(type = FieldType.Date)
    private Date createdAt;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Boolean)
    private boolean deleted;

    @Field(type = FieldType.Boolean)
    private boolean visible;

    @Field(type = FieldType.Boolean)
    private boolean restricted;

}
