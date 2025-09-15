package com.example.jsh.repository;

import com.example.jsh.entity.ImageFile;
import com.example.jsh.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ImageFileRepository extends JpaRepository<ImageFile, String> {
    List<ImageFile> findByOwnerOrderByCreatedTDesc(UserAccount owner);
    Optional<ImageFile> findByIdAndOwner(String id, UserAccount owner);
}
