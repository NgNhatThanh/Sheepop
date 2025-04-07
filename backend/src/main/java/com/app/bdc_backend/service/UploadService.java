package com.app.bdc_backend.service;

import com.app.bdc_backend.exception.ServerException;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile image){
        try {
            String url = this.cloudinary.uploader().upload(image.getBytes(),
                    Map.of("public_id", UUID.randomUUID().toString())).get("url").toString();
            return url;
        } catch (IOException e) {
            throw new ServerException("Server Error");
        }
    }

}
