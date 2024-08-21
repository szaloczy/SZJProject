package com.szj.common.fileServer.services;

import com.szj.common.fileServer.model.FileEntity;
import com.szj.common.fileServer.repository.FileServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServerService {

    private final FileProcessorService fileProcessorService;
    private final FileServerRepository fileServerRepository;
    /**
     * This method is used to upload a single file, process it, and save it in the repository.
     * After successful processing and saving of the file it returns the corresponding FileEntity.
     *
     * @param file The MultipartFile to be uploaded
     * @return FileEntity that represents the uploaded and saved file
     * @throws IOException If an I/O occurs during file upload
     */

    public FileEntity upload(MultipartFile file) throws IOException {
        FileEntity fileEntity = fileProcessorService.upload(file);
        fileServerRepository.save(fileEntity);
        return fileEntity;
    }

    public FileEntity setFileEntityInUse(UUID fileEntityId, boolean inUse) throws NoSuchElementException {
        Optional<FileEntity> fileEntity = fileServerRepository.findFileEntityById(fileEntityId);
        if(fileEntity.isEmpty()) {
            throw new NoSuchElementException("Could not find FileEntity");
        }
        fileEntity.get().setInUse(inUse);
        fileServerRepository.save(fileEntity.get());

        return fileEntity.get();
    }
}
