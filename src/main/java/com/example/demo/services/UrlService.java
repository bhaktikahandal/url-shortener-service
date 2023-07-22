package com.example.demo.services;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.models.UrlEntity;
import com.example.demo.repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;


import java.util.Optional;

@Service
public class UrlService {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARACTERS.length();
    private static final int SHORT_URL_LENGTH = 6;
    private static final String BASE_URL = "http://localhost:8080/";

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlMappingRepository) {
        this.urlRepository = urlMappingRepository;
    }

    public String shortenUrl(UrlRequest urlRequest) {
        String originalUrl = urlRequest.getOriginalUrl();
        String customUrl = urlRequest.getCustomUrl();
        if (customUrl != null && !customUrl.isEmpty()) {
            Optional<UrlEntity> existingMapping = Optional.ofNullable(urlRepository.findByShortUrl(customUrl));
            if (existingMapping.isPresent()) {
                throw new IllegalArgumentException("Custom short URL already exists.");
            }
        } else {
            customUrl = generateShortURL();
        }

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setShortUrl(customUrl);
        urlRepository.save(urlEntity);

        return BASE_URL + customUrl;
    }

    public String getOriginalUrl(String shortURL) {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortURL);
        if (urlEntity != null) {
            return urlEntity.getOriginalUrl();
        }
        return "URL not found";
    }

    private String generateShortURL() {
        String md5Hash = DigestUtils.md5Hex(String.valueOf(System.nanoTime()));
        StringBuilder shortURL = new StringBuilder();

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            String hexSubStr = md5Hash.substring(i * 4, i * 4 + 4);
            int decimalValue = Integer.parseInt(hexSubStr, 16);
            shortURL.append(CHARACTERS.charAt(decimalValue % BASE));
        }
        return shortURL.toString();
    }
}