package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.ProductRepository;
import com.app.bdc_backend.model.product.Product;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    public Optional<Product> findById(ObjectId id){
        return productRepository.findById(id);
    }


}
