package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.CheckoutFacaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout")
@Slf4j
public class CheckoutController {

    private final CheckoutFacaceService checkoutFacaceService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/get")
    public ResponseEntity<?> getCheckoutList(@RequestBody(required = false) Map<String, Object> rqBody){
        return ResponseEntity.ok(checkoutFacaceService.getCheckoutList(rqBody));
    }

}
