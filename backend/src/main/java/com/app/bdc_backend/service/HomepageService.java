package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.homepage.BannerRepository;
import com.app.bdc_backend.dao.homepage.DisplayCatRepository;
import com.app.bdc_backend.model.homepage.Banner;
import com.app.bdc_backend.model.homepage.DisplayCategory;
import com.app.bdc_backend.model.product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomepageService {

    private final BannerRepository bannerRepository;

    private final DisplayCatRepository displayCatRepository;

    public List<Banner> getAllBanners(){
        return bannerRepository.findAllByIdNotNull().orElse(List.of());
    }

    public List<DisplayCategory> getAllDisplayCategories(){
        return displayCatRepository.findAllByIdNotNull().orElse(List.of());
    }

    public Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner getBannerById(String bannerId) {
        return bannerRepository.findById(bannerId).orElse(null);
    }

    public DisplayCategory saveDisplayCategory(DisplayCategory displayCategory) {
        return displayCatRepository.save(displayCategory);
    }

    public DisplayCategory getDisplayCatByCat(Category category) {
        return displayCatRepository.findByCategory(category);
    }

    public void deleteDisplayCategory(DisplayCategory displayCategory) {
        displayCatRepository.delete(displayCategory);
    }

    public DisplayCategory getDisplayCatById(String disCatId) {
        return displayCatRepository.findById(disCatId).orElse(null);
    }
}
