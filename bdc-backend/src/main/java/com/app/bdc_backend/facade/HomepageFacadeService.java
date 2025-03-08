package com.app.bdc_backend.facade;

import com.app.bdc_backend.elasticsearch.service.ESProductService;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.request.ProductSearchFilters;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.service.CategoryService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomepageFacadeService {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final ESProductService esProductService;

    public Page<ProductCardDTO> getHomePageItems(int page, int limit){
        Page<Product> productPage = productService.findAllForHomepage(page, limit);
        return productPage.map(
                prod -> ModelMapper.getInstance().map(prod, ProductCardDTO.class));
    }

    public Page<ProductCardDTO> searchProducts(String keyword,
                                               String sortBy,
                                               String order,
                                               int page, int limit,
                                               ProductSearchFilters filters){
        if(!filters.getCategoryIds().isEmpty()){
            String parentCatId = filters.getCategoryIds().get(0);
            Category category = categoryService.findById(parentCatId);
            if(category == null)
                throw new RequestException("Invalid request: category not found");
            List<Category> parents = new ArrayList<>(List.of(category));
            boolean ok = true;
            while(ok){
                ok = false;
                for(Category cat : parents){
                    if(cat.isHasChildren()){
                        ok = true;
                        List<Category> par = categoryService.getByParent(cat);
                        parents.remove(cat);
                        parents.addAll(par);
                    }
                }
            }
            filters.setCategoryIds(parents.stream().map(cat -> cat.getId().toString()).toList());
        }
        return esProductService.homepageSearch(keyword, sortBy, order, page, limit, filters);
    }

}
