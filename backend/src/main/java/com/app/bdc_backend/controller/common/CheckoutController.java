package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.CheckoutFacaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class CheckoutController {

    private final CheckoutFacaceService checkoutFacaceService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/get")
    @Operation(
            summary = "Get checkout list, with optional address"
    )
    //body include addressId
    public ResponseEntity<?> getCheckoutList(@RequestBody(required = false) Map<String, Object> rqBody){
        return ResponseEntity.ok(checkoutFacaceService.getCheckoutList(rqBody));
    }

}
