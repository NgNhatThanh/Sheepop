package com.app.bdc_backend.facade;

import com.app.bdc_backend.elasticsearch.service.ESProductService;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.dto.request.ProductSearchFilters;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.model.homepage.Banner;
import com.app.bdc_backend.model.homepage.DisplayCategory;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.service.AddressService;
import com.app.bdc_backend.service.product.CategoryService;
import com.app.bdc_backend.service.HomepageService;
import com.app.bdc_backend.service.product.ProductService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HomepageFacadeService {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final ESProductService esProductService;

    private final AddressService addressService;

    private final HomepageService homepageService;

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
            List<Category> parents = categoryService.getAllByIdIn(filters.getCategoryIds());
            if(parents == null || parents.size() != filters.getCategoryIds().size()){
                throw new RequestException("Invalid request: some category id wrong");
            }
            boolean ok = true;
            List<Category> toRemove = new ArrayList<>();
            List<Category> toAdd = new ArrayList<>();
            while(ok){
                ok = false;
                for(Category cat : parents){
                    if(cat.isHasChildren()){
                        ok = true;
                        List<Category> par = categoryService.getByParent(cat);
                        toRemove.add(cat);
                        toAdd.addAll(par);
                    }
                }
                parents.removeAll(toRemove);
                parents.addAll(toAdd);
                toRemove.clear();
                toAdd.clear();
            }
            filters.setCategoryIds(parents.stream().map(cat -> cat.getId().toString()).toList());
        }
        return esProductService.homepageSearch(keyword, sortBy, order, page, limit, filters);
    }

    public List<Category> getCategoriesFilter(){
        return categoryService.getByParent(null);
    }

    public List<Province> getLocationsFilter(){
        return addressService.getProvinceList();
    }

    public List<Banner> getAllBanners() {
        return homepageService.getAllBanners();
    }

    public List<DisplayCategory> getAllDisplayCategories() {
        return homepageService.getAllDisplayCategories();
    }
}
