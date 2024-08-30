package com.szj.common.fileServer.controllers;

import com.szj.common.fileServer.model.FileEntity;
import com.szj.common.fileServer.services.FileServerService;
import com.szj.demo.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("api/file-server")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class FileServerController {

    private final FileServerService fileServerService;

    @PostMapping()
    public ResponseEntity<ApiResponse<FileEntity>> upload(@RequestParam("file") MultipartFile file) {
        try{
            long startTime = System.nanoTime();
            FileEntity fileEntity = fileServerService.upload(file);
            long endTime = System.nanoTime();
            double durationInSeconds = (endTime - startTime) / 1e9;
            System.out.println("Image upload, save, resize time: " + durationInSeconds + " seconds");
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, fileEntity, ""));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.toString()));
        }
    }
}
