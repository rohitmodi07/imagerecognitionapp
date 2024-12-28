package com.imagerecognition.imagerecognition.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@SuppressWarnings("unused")
public class ImageRecognitionConfig {

    @Value("${huggingface.api.url}")
    private String api_url;

    @Value("${huggingface.api.token}")
    private String api_token;

    @Bean
    public String api_url(){
        return this.api_url;
    }

    @Bean
    public String api_token(){
        return this.api_token;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
