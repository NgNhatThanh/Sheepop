package com.app.bdc_backend.controller.shop;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.ShopDashboardFacadeService;
import com.app.bdc_backend.model.dto.response.TaskOverviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop/dashboard")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class ShopDashboardController {

    private final ShopDashboardFacadeService shopDashboardFacadeService;

    @GetMapping("/get_task_overview")
    @Operation(summary = "Get shop tasks overview")
    public ResponseEntity<TaskOverviewDTO> getTaskOverview() {
        return ResponseEntity.ok(shopDashboardFacadeService.getTaskOverview());
    }

}
