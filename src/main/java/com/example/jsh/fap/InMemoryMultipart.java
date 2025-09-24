package com.example.jsh.fap;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

//디스크 저장 x
public class InMemoryMultipart extends ByteArrayResource {
    private final String filename;
    public InMemoryMultipart(String filename, MultipartFile src) throws IOException {
        super(src.getBytes());
        this.filename = filename;
    }
    @Override public String getFilename() { return filename; }
}
