package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.repository.ImageFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageFileRepository repo;

    @Transactional
    public int saveAll(List<MultipartFile> files) throws Exception {
        if (files == null || files.isEmpty()) return 0;

        int saved = 0;
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty() || !isAllowedContentType(f.getContentType())) continue;

            ImageFile img = ImageFile.builder()
                    .fileName(StringUtils.cleanPath(Objects.requireNonNull(f.getOriginalFilename())))
                    .contentType(f.getContentType())
                    .size(f.getSize())
                    .data(f.getBytes())      // BLOB
                    .createdT(Instant.now())
                    .build();

            repo.save(img);
            saved++;
        }
        return saved;
    }


    private boolean isAllowedContentType(String ct) {
        if (ct == null) return false;
        return ct.startsWith("image/");   // 필요하면 화이트리스트로 강화: jpeg, png, webp 등
    }
}

