package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ShopProductTableResponseDTO {

    private String id;

    private String name;

    private String thumbnailUrl;

    private long price;

    private int quantity;

    private long revenue;

    private int sold;

    private List<ProductSKUResponseDTO> skuList = new ArrayList<>();

    private boolean visible;

    private Date createdAt;

    private Date updatedAt;

}
