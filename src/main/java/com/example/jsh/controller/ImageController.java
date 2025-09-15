package com.example.jsh.web;

import com.example.jsh.entity.UserAccount;
import com.example.jsh.repository.UserAccountRepository;
import com.example.jsh.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserAccountRepository users;

    private UserAccount me(String username) {
        return users.findByUsername(username).orElseThrow();
    }

    @GetMapping("/gallery")
    public String gallery(@AuthenticationPrincipal User principal, Model model) {
        var owner = me(principal.getUsername());
        model.addAttribute("images", imageService.list(owner));
        return "gallery";
    }

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@AuthenticationPrincipal User principal,
                         @RequestParam("file") MultipartFile file,
                         Model model) {
        var owner = me(principal.getUsername());
        try {
            imageService.save(owner, file);
            return "redirect:/gallery";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("images", imageService.list(owner));
            return "gallery";
        }
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> stream(@AuthenticationPrincipal User principal,
                                           @PathVariable String id) {
        var owner = me(principal.getUsername());
        var img = imageService.getOwned(owner, id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + (img.getFileName() == null ? id : img.getFileName()) + "\"")
                .body(new ByteArrayResource(img.getData()));
    }

    @PostMapping("/images/{id}/delete")
    public String delete(@AuthenticationPrincipal User principal, @PathVariable String id) {
        var owner = me(principal.getUsername());
        imageService.deleteOwned(owner, id);
        return "redirect:/gallery";
    }
}
