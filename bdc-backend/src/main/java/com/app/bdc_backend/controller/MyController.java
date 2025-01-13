package com.app.bdc_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/abc")
    public String getAbc(){
        return "abc";
    }

}
