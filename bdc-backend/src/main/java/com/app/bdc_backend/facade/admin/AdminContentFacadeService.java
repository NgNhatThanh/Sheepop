package com.app.bdc_backend.facade.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.dto.request.BannerDTO;
import com.app.bdc_backend.model.dto.request.UpdateDisplayCategoryDTO;
import com.app.bdc_backend.model.homepage.Banner;
import com.app.bdc_backend.model.homepage.DisplayCategory;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.service.CategoryService;
import com.app.bdc_backend.service.HomepageService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminContentFacadeService {

    private final HomepageService homepageService;

    private final CategoryService categoryService;

    public List<Banner> getBanners() {
        return homepageService.getAllBanners();
    }

    public List<DisplayCategory> getDisplayCategories() {
        return homepageService.getAllDisplayCategories();
    }

    public Banner addBanner(BannerDTO dto) {
        Banner banner = ModelMapper.getInstance().map(dto, Banner.class);
        return homepageService.saveBanner(banner);
    }

    public Banner updateBanner(String bannerId, BannerDTO dto) {
        Banner banner = homepageService.getBannerById(bannerId);
        if(banner == null) {
            throw new RequestException("Banner not found");
        }
        Banner updatedBanner = ModelMapper.getInstance().map(dto, Banner.class);
        updatedBanner.setId(banner.getId());
        return homepageService.saveBanner(banner);
    }

    public List<Category> getParentCategories() {
        return categoryService.getByParent(null);
    }

    public DisplayCategory addDisplayCategory(String categoryId) {
        Category category = categoryService.getById(categoryId);
        if(category == null) {
            throw new RequestException("Category not found");
        }
        DisplayCategory displayCategory = new DisplayCategory();
        displayCategory.setCategory(category);
        return homepageService.saveDisplayCategory(displayCategory);
    }


    public void deleteDisplayCategory(String categoryId) {
        Category category = categoryService.getById(categoryId);
        if(category == null) {
            throw new RequestException("Category not found");
        }
        DisplayCategory displayCategory = homepageService.getDisplayCatByCat(category);
        if(displayCategory == null) {
            throw new RequestException("Display category not found");
        }
        homepageService.deleteDisplayCategory(displayCategory);
    }

    public DisplayCategory updateDisplayCategory(String disCatId, UpdateDisplayCategoryDTO dto) {
        DisplayCategory displayCategory = homepageService.getDisplayCatById(disCatId);
        if(displayCategory == null) {
            throw new RequestException("Display category not found");
        }
        displayCategory.setThumbnailUrl(dto.getThumbnailUrl());
        return homepageService.saveDisplayCategory(displayCategory);
    }
}
