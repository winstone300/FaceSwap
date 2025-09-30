package com.example.jsh.controller;

import com.example.jsh.entity.UserAccount;
import com.example.jsh.fap.FuseAnyPartClient;
import com.example.jsh.repository.UserAccountRepository;
import com.example.jsh.service.ImageService;
import com.example.jsh.service.ImageWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserAccountRepository users;
    private final FuseAnyPartClient fapClient;
    private final ImageWriteService imageWriteService;

    private UserAccount me(User principal) {
        return users.findByUsername(principal.getUsername())
                .orElseThrow();
    }


    @GetMapping("/gallery")
    public String gallery(@AuthenticationPrincipal User principal, Model model) {
        var owner = me(principal);
        model.addAttribute("images", imageService.list(owner));
        return "gallery";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> upload(
            @RequestParam("face") MultipartFile face,
            @RequestParam(value="eyes", required=false) MultipartFile eyes,
            @RequestParam(value="nose", required=false) MultipartFile nose,
            @RequestParam(value="mouth", required=false) MultipartFile mouth,
            @RequestParam(value="steps", defaultValue="50") int steps,
            @RequestParam(value="guidance", defaultValue="7.5") double guidance,
            @AuthenticationPrincipal User principal
    ) {
        var owner = me(principal);
        if (face == null || face.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false, "error", "Face 이미지가 필요합니다."
            ));
        }

        String parts = "face,eyes,nose,mouth";
        Path result = null;
        try {
            result = fapClient.infer(face, eyes, nose, mouth, parts, steps, guidance);
            var save = imageWriteService.saveResultPng(owner, result, "fap-result.png");
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "id", save.id(),
                    "viewUrl", "/images/" + save.id()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "ok", false, "error", "FuseAnyPart 처리 실패: " + e.getMessage()
            ));
        } finally {
            try { if (result != null) java.nio.file.Files.deleteIfExists(result); } catch (Exception ignore) {}
        }
    }



    @GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<Resource> readOwned(@AuthenticationPrincipal User principal,
                                              @PathVariable String id) {
        return imageService.streamOwned(me(principal), id);
    }

    @PostMapping("/gallery/delete")
    public String delete(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                         @RequestParam("ids") java.util.List<String> ids,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        var owner = me(principal);
        imageService.deleteOwned(owner, ids);
        ra.addFlashAttribute("message", ids.size() + "개 삭제 완료");
        return "redirect:/gallery";
    }
}
