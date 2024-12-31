package com.imagerecognition.imagerecognition.service;

import com.imagerecognition.imagerecognition.model.ImageDescriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ImageRecognitionService {

    @Autowired
    private String api_url;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Object> describeImage(byte[] fileData, String apikey){

        try {
            // Set the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apikey);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

            // Prepare request entity to send in the request
            HttpEntity<byte[]> entity = new HttpEntity<>(fileData, headers);

            // Send the POST request to HuggingFace image to description API
            ResponseEntity<List> response = restTemplate.exchange(api_url, HttpMethod.POST, entity, List.class);

            // Return the response
            return ResponseEntity.ok(response.getBody().get(0));
        } catch (HttpClientErrorException e) {
            // Handle exceptions and return error message
            return org.springframework.http.ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

    }

    public String generateTextForImage(byte[] fileData, String apikey){

        try {
            // Set the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apikey);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

            // Prepare request entity to send in the request
            HttpEntity<byte[]> entity = new HttpEntity<>(fileData, headers);

            // Send the POST request to HuggingFace image to description API
            ResponseEntity<ImageDescriptionResponse[]> response = restTemplate.exchange(api_url, HttpMethod.POST, entity, ImageDescriptionResponse[].class);
            List<ImageDescriptionResponse> responseText = Arrays.asList(response.getBody());
            System.out.println(" response :::: "+responseText.get(0).generated_text());
            // Return the response
            return responseText.get(0).generated_text();
        } catch (HttpClientErrorException e) {
            // Handle exceptions and return error message
            throw new RuntimeException(" error :::: " + e.getMessage());
        }

    }

    public ResponseEntity<byte[]> generateImageWithText(MultipartFile fileData, String message,
                                                        String apikey) throws IOException {
        // Convert MultipartFile to BufferedImage
        BufferedImage originalImage = ImageIO.read(fileData.getInputStream());

        // Create a Graphics object to draw on the image
        Graphics2D graphics2D = originalImage.createGraphics();

        // Set font and color for the text
        graphics2D.setFont(new Font("Arial", Font.BOLD, 50));
        graphics2D.setColor(Color.RED);
        if(message == null || message.isEmpty()){
            message = generateTextForImage(fileData.getBytes(), apikey);
        }

        // Calculate the coordinates to center the text
        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        // Draw the text on the image
        Font currentFont = graphics2D.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.3F);
        graphics2D.setFont(newFont);
        graphics2D.drawString(message, 10, fontMetrics.getAscent());
        graphics2D.dispose(); // Always dispose of the Graphics object

        // Convert the BufferedImage back to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "PNG", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        // Set the content type and return the image as a response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
