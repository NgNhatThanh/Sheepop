package com.app.bdc_backend.controller;

import com.app.bdc_backend.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile image) {
        return ResponseEntity.ok().body(Map.of(
                "url", uploadService.uploadImage(image)
        ));
    }

}
