package com.example.jsh.controller;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.service.ImageReadService;
import com.example.jsh.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class controller {
    private final ImageService imageService;
    private final ImageReadService imageReadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResult upload(@RequestPart("file") MultipartFile file) throws Exception {
        ImageFile saved = imageService.saveRaw(file);
        return new UploadResult(saved.getId(), saved.getFileName(), saved.getContentType(), saved.getSize());
    }

    public record UploadResult(String id, String originalName, String contentType, long size) {}

    @GetMapping("images/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable String id){
        return imageReadService.streamById(id);
    }
}
