package com.app.bdc_backend.dao.homepage;

import com.app.bdc_backend.model.homepage.DisplayCategory;
import com.app.bdc_backend.model.product.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisplayCatRepository extends MongoRepository<DisplayCategory, String> {

    Optional<List<DisplayCategory>> findAllByIdNotNull();

    DisplayCategory findByCategory(Category category);
}
