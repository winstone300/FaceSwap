package com.example.jsh.fap;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FAPController {

    private final FuseAnyPartClient client;

    @GetMapping("/fap")
    public String form() { return "fap_form"; }

    @PostMapping(value="/fap", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String run(
            @RequestParam("target") MultipartFile target,
            @RequestParam(value="eyes", required=false) MultipartFile eyes,
            @RequestParam(value="nose", required=false) MultipartFile nose,
            @RequestParam(value="mouth", required=false) MultipartFile mouth,
            @RequestParam(value="parts", required=false) String parts,
            @RequestParam(defaultValue="50") int steps,
            @RequestParam(name="guidance", defaultValue="7.5") double guidance,
            Model model
    ) throws Exception {
        Path result = client.infer(target, eyes, nose, mouth, parts, steps, guidance);
        String token = UUID.randomUUID().toString(); // 간단 토큰
        model.addAttribute("imgPath", result.toString());
        model.addAttribute("token", token);
        return "fap_result";
    }

    @GetMapping("/fap/image/tmp")
    public ResponseEntity<Resource> image(@RequestParam("p") String p) throws Exception {
        Path path = Path.of(p);
        if (!Files.exists(path)) return ResponseEntity.notFound().build();
        var res = new FileSystemResource(path.toFile());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(res);
    }
}
