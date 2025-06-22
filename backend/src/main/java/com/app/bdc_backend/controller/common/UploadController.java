package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class UploadController {

    private final UploadService uploadService;

    @Value("${file.image.max-size}")
    private long maxImageSize;

    @PostMapping("/image")
    @ResponseBody
    @Operation(summary = "Upload image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile image) {
        if(image.getSize() > maxImageSize) {
            throw new RequestException("Image size too large");
        }
        return ResponseEntity.ok(Map.of("url", uploadService.uploadImage(image)));
    }

}
