package com.example.demo.services;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.models.UrlEntity;
import com.example.demo.models.UserEntity;
import com.example.demo.repositories.UrlRepository;
import com.example.demo.utils.AppException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UrlService {
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    private static final int DEFAULT_EXPIRATION_DAYS = 365;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARACTERS.length();
    private static final int SHORT_URL_LENGTH = 7;

    private static final String BASE_URL = "http://localhost:8080/";
    private final UrlRepository urlRepository;
    private final IdGenerationService idGenerationService;
    private final UserService userService;

    @Autowired
    public UrlService(UrlRepository urlMappingRepository, IdGenerationService idGenerationService, UserService userService) {
        this.urlRepository = urlMappingRepository;
        this.idGenerationService = idGenerationService;
        this.userService = userService;
    }

    @Async
    public CompletableFuture<String> shortenUrl(UrlRequest urlRequest) {
        String originalUrl = urlRequest.getOriginalUrl();
        String customUrl = urlRequest.getCustomUrl();
        Timestamp now = Timestamp.from(Instant.now());

        if (!isValidUrl(originalUrl))
            throw new AppException("Invalid Url:", originalUrl, 106);
        long userId = urlRequest.getUserId();
        UserEntity user = userService.fetchByUserId(userId);

        UrlEntity existingUrl = urlRepository.findByOriginalUrlAndUserId(originalUrl, userId);
        if (existingUrl != null) {
            return CompletableFuture.completedFuture(BASE_URL + existingUrl.getShortUrl());
        }

        if (customUrl != null && !customUrl.isEmpty() && isValidCustomUrl(customUrl)) {
            checkIfCustomUrlExsist(customUrl);
        } else {
            customUrl = generateShortURL();
        }
        Timestamp expirationTime = Timestamp.from(now.toInstant().plus(DEFAULT_EXPIRATION_DAYS, ChronoUnit.DAYS));
        UrlEntity urlEntity = UrlEntity.builder()
                .originalUrl(originalUrl).shortUrl(customUrl)
                .createdAt(now)
                .expirationTime(expirationTime).userEntity(user).build();

        log.info("Generated Short Url: {} for original Url: {}", customUrl, originalUrl);
        urlRepository.save(urlEntity);

        return CompletableFuture.completedFuture(BASE_URL + customUrl);
    }

    private void checkIfCustomUrlExsist(String customUrl) {
        Optional<UrlEntity> existingMapping = Optional.ofNullable(urlRepository.findByShortUrl(customUrl));
        if (existingMapping.isPresent()) {
            throw new AppException("Custom short URL already exists.", customUrl, 101);
        }
    }

    private boolean isValidUrl(String originalUrl) {
        try {
            URL url = new URL(originalUrl);
            String protocol = url.getProtocol();
            return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);
        } catch (MalformedURLException e) {
            throw new AppException("Malformed URL", originalUrl, 103);
        }
    }

    private boolean isValidCustomUrl(String customUrl) {
        for (char c : customUrl.toCharArray()) {
            if (CHARACTERS.indexOf(c) == -1) {
                throw new AppException("Invalid Custom Url", customUrl, 105);
            }
        }
        return true;
    }


    @Async
    public CompletableFuture<String> getOriginalUrl(String shortURL) {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortURL);
        if (urlEntity != null) {
            if (urlEntity.getExpirationTime().before(Timestamp.from(Instant.now()))) {
                urlRepository.deleteById(urlEntity.getId());
                throw new AppException("URL expired", shortURL, 109);
            }
            String originalUrl = urlEntity.getOriginalUrl();
            log.info("Found Original Url {} for short Url {}", originalUrl, shortURL);
            return CompletableFuture.completedFuture(originalUrl);
        }
        log.error("URL not exists :{}", shortURL);
        throw new AppException("URL not exists", shortURL, 108);
    }

    private String generateShortURL() {
        String uniqueId = idGenerationService.generateId();
        return base64Encode(uniqueId);
    }

    private String base64Encode(String input) {
        byte[] bytes = input.getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : encodedBytes) {
            int i = b & 0xFF;
            sb.append(CHARACTERS.charAt(i % BASE));
        }
        return sb.substring(0, SHORT_URL_LENGTH);
    }

    public void cleanExpiredUrls() {
        List<UrlEntity> expiredUrls = urlRepository.findByExpirationTimeBefore(Timestamp.from(Instant.now()));
        log.info("Expired urls count: {} ", expiredUrls.size());
        urlRepository.deleteAll(expiredUrls);
    }
}