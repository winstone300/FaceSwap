package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.entity.UserAccount;
import com.example.jsh.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageFileRepository repo;

    @Transactional
    public ImageFile save(UserAccount owner, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("파일이 비어 있습니다.");

        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/png") || ct.equals("image/jpeg") || ct.equals("image/webp"))) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.(png/jpeg/webp)");
        }

        ImageFile img = ImageFile.builder()
                .id(null) // UUID 자동 생성(JPA 3 / Hibernate 6)
                .owner(owner)
                .fileName(file.getOriginalFilename())
                .contentType(ct)
                .size(file.getSize())
                .data(file.getBytes())
                .createdT(Instant.now())
                .build();

        return repo.save(img);
    }

    @Transactional(readOnly = true)
    public List<ImageFile> list(UserAccount owner) {
        return repo.findByOwnerOrderByCreatedTDesc(owner);
    }

    @Transactional(readOnly = true)
    public ImageFile getOwned(UserAccount owner, String id) {
        return repo.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteOwned(UserAccount owner, String id) {
        ImageFile img = getOwned(owner, id);
        repo.delete(img);
    }
}
