package com.app.bdc_backend.controller;

import com.app.bdc_backend.entity.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MyController {

//    @GetMapping("/test")
//    public ResponseEntity<Test> test(){
//        return new ResponseEntity<>(new Test("abc"), HttpStatus.OK);
//    }

}
