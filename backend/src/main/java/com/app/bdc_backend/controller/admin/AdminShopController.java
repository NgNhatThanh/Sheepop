package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.admin.AdminShopFacadeService;
import com.app.bdc_backend.model.dto.response.AdminShopTableDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/shop")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminShopController {

    private final AdminShopFacadeService adminShopFacadeService;

    @GetMapping("/get_list")
    @Operation(
            summary = "Get list of all shops with filters and sorting"
    )
    public ResponseEntity<PageResponse<AdminShopTableDTO>> getShopList(@RequestParam(value = "type") int type,
                                         @RequestParam(value = "filterType", defaultValue = "0") int filterType,
                                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                         @RequestParam int sortType,
                                         @RequestParam(value = "page") int page,
                                         @RequestParam (value = "limit") int limit){
        log.info("Keyword: {}", keyword);
        Page<AdminShopTableDTO> dtos = adminShopFacadeService.getShopList(
                type,
                filterType,
                keyword,
                sortType,
                page,
                limit
        );
        PageResponse<AdminShopTableDTO> response = new PageResponse<>(dtos);
        return ResponseEntity.ok(response);
    }

}
