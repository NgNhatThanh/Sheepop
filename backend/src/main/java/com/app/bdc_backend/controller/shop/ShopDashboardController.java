package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.facade.ShopDashboardFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/dashboard")
public class ShopDashboardController {

    private final ShopDashboardFacadeService shopDashboardFacadeService;

    @GetMapping("/get_task_overview")
    public ResponseEntity<?> getTaskOverview() {
        return ResponseEntity.ok(shopDashboardFacadeService.getTaskOverview());
    }

}
