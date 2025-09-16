package com.example.jsh.controller;

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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserAccountRepository users;

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@AuthenticationPrincipal User principal,
                         @RequestParam("files") List<MultipartFile> files,
                         Model model) {
        var owner = me(principal);
        for(MultipartFile file:files){
            try {
                imageService.save(owner, file);
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("images", imageService.list(owner));
            }
        }
        return "redirect:/gallery";
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
