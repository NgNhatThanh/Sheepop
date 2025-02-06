package com.app.bdc_backend.controller;

import com.cloudinary.Cloudinary;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
@AllArgsConstructor
public class UploadController {

    private Cloudinary cloudinary;

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile image) {
        Map<String, String> response = new HashMap<>();
        try {
            String url = this.cloudinary.uploader().upload(image.getBytes(),
                    Map.of("public_id", UUID.randomUUID().toString())).get("url").toString();
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Something wrong, try again later!");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
