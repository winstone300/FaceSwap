package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageReadService {
    private final ImageFileRepository repo;

    @Transactional(readOnly = true) // LAZY로 둔 BLOB 로딩을 트랜잭션 안에서 안전하게 수행
    public ResponseEntity<Resource> streamById(String id) {
        ImageFile f = repo.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("이미지가 없습니다: " + id));

        byte[] data = f.getData();
        String ct = f.getContentType() != null ? f.getContentType() : "application/octet-stream";
        String name = f.getFileName() != null ? f.getFileName() : id + guessExt(ct);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name + "\"")
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    private String guessExt(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}