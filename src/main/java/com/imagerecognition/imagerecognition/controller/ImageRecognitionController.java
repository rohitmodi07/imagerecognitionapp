package com.imagerecognition.imagerecognition.controller;

import com.imagerecognition.imagerecognition.service.ImageRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageRecognitionController {
    @Autowired
    private ImageRecognitionService imageRecognitionService;

    @PostMapping(value = "/imgRecognize", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> describeImage(@RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        return imageRecognitionService.describeImage(file.getBytes());
    }

    @PostMapping(value = "/formImageWithText", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<byte[]> generateMessageAndPutOnImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "message", required = false) String message) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded".getBytes());
        }
        return imageRecognitionService.generateImageWithText(file, message);
    }
}
