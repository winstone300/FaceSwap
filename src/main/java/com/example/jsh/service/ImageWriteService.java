package com.example.jsh.service;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.entity.UserAccount;
import com.example.jsh.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ImageWriteService {

    private final ImageFileRepository repo;

    public Saved saveResultPng(UserAccount owner, Path pngFile, String filename) throws Exception {
        byte[] data = Files.readAllBytes(pngFile);
        ImageFile img = ImageFile.builder()
                .owner(owner)
                .fileName(filename)
                .contentType("image/png")
                .size(data.length)
                .data(data)
                .build();
        ImageFile saved = repo.save(img);
        return new Saved(saved.getId(), saved.getFileName());
    }

    public record Saved(String id, String name) {}
}
