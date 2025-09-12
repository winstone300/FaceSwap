package com.example.jsh.controller;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.repository.ImageFileRepository;
import com.example.jsh.service.ImageReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class controller {

    private final ImageFileRepository repo;
    private final ImageReadService imageReadService;

    @GetMapping({"/", "/upload"})
    public String uploadForm() {
        return "upload"; // templates/upload.html
    }

    @PostMapping("/upload")
    @Transactional
    public String handleUpload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            RedirectAttributes ra
    ) throws Exception {

        List<MultipartFile> all = new ArrayList<>();
        if (file != null && !file.isEmpty()) all.add(file);
        if (files != null) files.stream().filter(f -> !f.isEmpty()).forEach(all::add);

        if (all.isEmpty()) {
            ra.addFlashAttribute("error", "업로드할 파일이 없습니다.");
            return "redirect:/upload";
        }

        int saved = 0;
        for (MultipartFile f : all) {
            ImageFile img = ImageFile.builder()
                    // id는 @GeneratedValue(UUID) 이므로 건드리지 않음
                    .fileName(StringUtils.cleanPath(Objects.requireNonNull(f.getOriginalFilename())))
                    .contentType(f.getContentType())
                    .size(f.getSize())
                    .data(f.getBytes())        // BLOB
                    .createdT(Instant.now())
                    .build();
            repo.save(img); // 저장 시 id 자동 생성
            saved++;
        }

        ra.addFlashAttribute("message", saved + "개 파일 업로드 완료");
        return "redirect:/gallery";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        // 최신순 정렬 (createdT 기준)
        List<ImageFile> images = repo.findAll(Sort.by(Sort.Direction.DESC, "createdT"));
        model.addAttribute("images", images);
        return "gallery"; // templates/gallery.html
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<Resource> stream(@PathVariable String id) {
        return imageReadService.streamById(id);
    }
}
