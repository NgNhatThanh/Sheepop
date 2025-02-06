package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.ProductMedia;
import com.app.bdc_backend.model.product.ProductSKU;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@ToString
public class AddProductDTO {

    private String name;

    private String description;

    private String thumbnailUrl;

    private long price;

    private int quantity;

    private Category category;

    private List<ProductSKU> skuList;

    private List<ProductMedia> mediaList;

    private boolean visible;

}
