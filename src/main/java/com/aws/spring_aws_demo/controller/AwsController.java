package com.aws.spring_aws_demo.controller;

import com.aws.spring_aws_demo.enumeration.FileType;
import com.aws.spring_aws_demo.service.AwsService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import lombok.val;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("/s3bucketStorage")
public class AwsController {

    @Autowired
    private AwsService service;

    @GetMapping("/{bucketName}")
    public ResponseEntity<?> listFiles(
            @PathVariable("bucketName")
            String bucketName
    ) {
        val body = service.listFiles(bucketName);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{bucketName}/upload")
    @SneakyThrows(IOException.class)
    public ResponseEntity<?> uploadFile(
            @PathVariable("bucketName")
            String bucketName,
            @RequestParam("file")
            MultipartFile file
    ) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is Empty");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        service.uploadFile(bucketName, fileName, fileSize, contentType, inputStream);

        return ResponseEntity.ok().body("File uploaded successfully");
    }

    @SneakyThrows
    @GetMapping("/{bucketName}/download/{fileName}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("bucketName")
            String bucketName,
            @PathVariable("fileName")
            String fileName
    ) {
        val body = service.downloadFile(bucketName, fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(FileType.fromFileName(fileName)).
                body(body.toByteArray());
    }

    @DeleteMapping("/{bucketName}/{fileName}")
    public ResponseEntity<?> deleteFile(
            @PathVariable("bucketName")
            String bucketName,
            @PathVariable("fileName")
            String fileName
    ) {
        service.deleteFiles(bucketName, fileName);
        return ResponseEntity.ok().build();
    }
}
