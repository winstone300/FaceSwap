package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageFileRepository repo;

    public ImageFile saveRaw(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        String ct = file.getContentType();
        if ("image/jpg".equalsIgnoreCase(ct)) {
            ct = "image/jpeg";   // 표준으로 통일
        }

        ImageFile entity = ImageFile.builder()
                .fileName(file.getOriginalFilename())
                .contentType(ct)
                .size(file.getSize())
                .data(file.getBytes())        // 처리 없이 원본 바이트 그대로 저장
                .createdT(Instant.now())
                .build();
        return repo.save(entity);
    }
}

