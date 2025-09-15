package com.example.jsh.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "image_file")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="owner_id")
    private UserAccount owner;


    private String fileName;    // 파일 명
    private String contentType; // 파일 타입 (jpg,png)
    private long size;          // 파일 크기

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] data;        // 실제 이미지 바이트

    private Instant createdT;   // 생성 시간
}
