package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.entity.UserAccount;
import com.example.jsh.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
    public void deleteOwned(UserAccount owner, List<String> ids) {
        for (String id : ids) {
            ImageFile img = getOwned(owner, id);
            repo.delete(img);
        }
    }

    /** ★ 소유자 검증 후 스트리밍 */
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> streamOwned(UserAccount owner, String id) {
        ImageFile file = getOwned(owner, id); // 여기서 owner 검증 완료
        ByteArrayResource body = new ByteArrayResource(file.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + UriUtils.encode(file.getFileName(), StandardCharsets.UTF_8) + "\"")
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePrivate())
                .body(body);
    }
}
