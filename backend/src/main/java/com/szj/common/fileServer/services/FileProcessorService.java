package com.szj.common.fileServer.services;

import com.szj.common.fileServer.model.FileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessorService {

    private final Path root = Paths.get("").toAbsolutePath().resolve("FileServerStorage");


    private final FileWrapperService fileWrapperService;

    //Dimensions for the resized image
    private final int IMAGE_WIDTH = 500;
    private final int IMAGE_HEIGHT = 500;

    public FileEntity upload(MultipartFile multipartFile) throws IOException, IllegalArgumentException {
        if(multipartFile.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File name can't be null");
        }
            String fileExtension = "." + getFileExtension(multipartFile.getOriginalFilename());
            switch (fileExtension) {
                case ".png" : case ".jpg" : case ".jpeg" : return processImage(multipartFile, fileExtension);
                default : return processDefault(multipartFile, fileExtension);
            }

    }

    private FileEntity processDefault(MultipartFile multipartFile, String fileExtension) throws IOException{
        FileEntity fileEntity = new FileEntity(UUID.randomUUID(), fileExtension, LocalDateTime.now());
        Path filePath = root.resolve(fileEntity.getId().toString() + fileExtension);
        try {
            fileWrapperService.write(filePath, multipartFile.getBytes());
        } catch (IOException e){
            throw new IOException("Error while writing file");
        }
        return fileEntity;
    }

    private FileEntity processImage(MultipartFile multipartFile, String fileExtension) throws IOException{
        FileEntity fileEntity = new FileEntity(UUID.randomUUID(), fileExtension, LocalDateTime.now());
        Path filePath = root.resolve(fileEntity.getId().toString() + fileExtension);

        try{
            int resizedImageExtension = fileExtension == "png" ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

            BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
            BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, resizedImageExtension);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);

            String watermark = "SzjProject";
            Font watermarkFont = new Font("Arial", Font.BOLD, 40);
            g2d.setFont(watermarkFont);
            g2d.setColor(new Color(30,30,30,128));
            for(int dx = -2; dx <= 2; dx++){
                for(int dy = -2; dy <= 2; dy++){
                    if(dx != 0 || dy != 0){
                        g2d.drawString(watermark, 10 + dx, resizedImage.getHeight() - 20 + dy);
                    }
                }
            }

            g2d.setColor(new Color(192,192,192,128));
            g2d.drawString(watermark, 10, resizedImage.getHeight() - 20);

            g2d.dispose();

            byte[] resizedImageBytes = getBytesFromImage(resizedImage, "png");
            fileWrapperService.write(filePath, resizedImageBytes);
        } catch (IOException e) {
            throw new IOException("Image resize failed");
        }
        return fileEntity;
    }

    private byte[] getBytesFromImage(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    private String getFileExtension(String filename) {
        if(filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
