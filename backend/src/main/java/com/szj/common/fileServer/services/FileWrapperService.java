package com.szj.common.fileServer.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileWrapperService {

    private final Path root = Paths.get("").toAbsolutePath().resolve("FileServerStorage");

    public Path write(Path filePath, byte[] content) throws IOException {
        if(!Files.exists(root)) {
            Files.createDirectories(root);
        }
        return Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}
