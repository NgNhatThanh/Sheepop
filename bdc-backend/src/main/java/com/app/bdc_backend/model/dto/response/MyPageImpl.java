package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.Product;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MyPageImpl<T> {

    private int totalElements;

    private List<T> content;

    public MyPageImpl(Document document, MongoTemplate mongoTemplate) {
        if(document == null){
            this.totalElements = 0;
            this.content = new ArrayList<>();
        }
        else{
            this.totalElements = document.getInteger("totalElements", 0);
            List<Document> productDocs = (List<Document>) document.get("content");
            List<Product> products = productDocs.stream()
                    .map(doc -> mongoTemplate.getConverter().read(Product.class, doc))
                    .toList();
            this.content = (List<T>) products;
        }
    }

}
