package com.example.demo.services;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.models.UrlEntity;
import com.example.demo.models.UserEntity;
import com.example.demo.repositories.UrlRepository;
import com.example.demo.utils.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private IdGenerationService idGenerationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenUrlWithValidUrl() {
        String originalUrl = "https://example.com";
        String customUrl = null;
        long userId = 123;
        UrlRequest urlRequest = new UrlRequest(originalUrl, customUrl, userId);
        UserEntity user = UserEntity.builder().userId(userId).build();
        when(userService.fetchByUserId(userId)).thenReturn(user);
        when(urlRepository.findByOriginalUrlAndUserId(originalUrl, userId)).thenReturn(null);
        when(idGenerationService.generateId()).thenReturn("uniqueId");
        when(urlRepository.findByShortUrl(anyString())).thenReturn(null);

        CompletableFuture<String> result = urlService.shortenUrl(urlRequest);

        assertNotNull(result);
        assertEquals("http://localhost:8080/Mz1YLAy", result.join());
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    void testShortenUrlWithExistingUrl() {
        String originalUrl = "https://example.com";
        String customUrl = null;
        long userId = 123;
        UrlRequest urlRequest = new UrlRequest(originalUrl, customUrl, userId);
        UserEntity user = UserEntity.builder().userId(userId).build();
        when(userService.fetchByUserId(userId)).thenReturn(user);

        UrlEntity existingUrl = UrlEntity.builder().shortUrl("abc").build();
        when(urlRepository.findByOriginalUrlAndUserId(originalUrl, userId)).thenReturn(existingUrl);

        CompletableFuture<String> result = urlService.shortenUrl(urlRequest);

        assertNotNull(result);
        assertEquals("http://localhost:8080/abc", result.join());
        verify(urlRepository, times(0)).save(any(UrlEntity.class));
    }

    @Test
    void testShortenUrlWithCustomUrl() {
        String originalUrl = "https://example.com";
        String customUrl = "custom";
        long userId = 123;
        UrlRequest urlRequest = new UrlRequest(originalUrl, customUrl, userId);
        UserEntity user = UserEntity.builder().userId(userId).build();
        when(userService.fetchByUserId(userId)).thenReturn(user);
        when(urlRepository.findByShortUrl(customUrl)).thenReturn(null);

        CompletableFuture<String> result = urlService.shortenUrl(urlRequest);

        assertNotNull(result);
        assertEquals("http://localhost:8080/custom", result.join());
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    void testShortenUrlWithInvalidCustomUrl() {
        String originalUrl = "https://example.com";
        String customUrl = "invalid_url";
        long userId = 123;
        UrlRequest urlRequest = new UrlRequest(originalUrl, customUrl, userId);

        assertThrows(AppException.class, () -> urlService.shortenUrl(urlRequest));
    }

    @Test
    void testGetOriginalUrlWithExistingShortUrl() {
        String shortURL = "abc";
        UrlEntity urlEntity = UrlEntity.builder().originalUrl("https://example.com")
                .expirationTime(Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS))).build();
        when(urlRepository.findByShortUrl(shortURL)).thenReturn(urlEntity);

        CompletableFuture<String> result = urlService.getOriginalUrl(shortURL);

        assertNotNull(result);
        assertEquals("https://example.com", result.join());
    }

    @Test
    void testGetOriginalUrlWithExpiredShortUrl() {
        String shortURL = "abc";
        UrlEntity urlEntity = UrlEntity.builder().originalUrl("https://example.com")
                .expirationTime(Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS))).build();
        when(urlRepository.findByShortUrl(shortURL)).thenReturn(urlEntity);

        assertThrows(AppException.class, () -> urlService.getOriginalUrl(shortURL));
        verify(urlRepository, times(1)).deleteById(urlEntity.getId());
    }

    @Test
    void testGetOriginalUrlWithNonExistingShortUrl() {
        String shortURL = "abc";
        when(urlRepository.findByShortUrl(shortURL)).thenReturn(null);

        assertThrows(AppException.class, () -> urlService.getOriginalUrl(shortURL));
    }
}