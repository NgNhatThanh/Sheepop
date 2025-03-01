package com.app.bdc_backend.dao.product;

import com.app.bdc_backend.model.product.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Category findByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);

    int countByParent(Category parent);
}
