package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.services.UrlService;
import com.example.demo.utils.AppException;

import com.example.demo.utils.AppResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenUrlWithValidRequest() throws Exception {
        UrlRequest urlRequest = new UrlRequest("https://example.com", null, 123);
        String expectedShortUrl = "http://localhost:8080/b";
        when(urlService.shortenUrl(urlRequest)).thenReturn(CompletableFuture.completedFuture(expectedShortUrl));

        ResponseEntity<AppResponse> response = urlController.shortenUrl(urlRequest).get();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedShortUrl, response.getBody().getData());
    }

    @Test
    void testShortenUrlWithInvalidRequest() throws Exception {
        UrlRequest urlRequest = new UrlRequest("invalid_url", null, 123);
        AppException appException = new AppException("Malformed URL", "invalid_url", 103);
        when(urlService.shortenUrl(urlRequest)).thenThrow(appException);

        ResponseEntity<AppResponse> response = urlController.shortenUrl(urlRequest).get();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(appException.toString(), response.getBody().getError());
    }

    @Test
    void testGetOriginalUrlWithExistingShortUrl() throws Exception {
        String shortUrl = "abc";
        String originalUrl = "https://example.com";
        when(urlService.getOriginalUrl(shortUrl)).thenReturn(CompletableFuture.completedFuture(originalUrl));

        ResponseEntity<String> response = urlController.getOriginalUrl(shortUrl).get();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(originalUrl, response.getHeaders().getFirst("Location"));
    }

    @Test
    void testGetOriginalUrlWithNonExistingShortUrl() throws Exception {
        String shortUrl = "non_existing_url";
        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new AppException("URL not exists", shortUrl, 108));

        ResponseEntity<String> response = urlController.getOriginalUrl(shortUrl).get();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCleanExpiredUrls() {
        ResponseEntity<String> response = urlController.cleanExpiredUrls();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Expired URLs deleted successfully.", response.getBody());
        verify(urlService, times(1)).cleanExpiredUrls();
    }
}