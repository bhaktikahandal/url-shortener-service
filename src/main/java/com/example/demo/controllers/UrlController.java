package com.example.demo.controllers;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.shortenUrl(urlRequest);
        return new ResponseEntity<>(shortUrl, HttpStatus.OK);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortUrl) {
        String originalUrl = urlService.getOriginalUrl(shortUrl);
        return originalUrl != null
                ? ResponseEntity.status(HttpStatus.FOUND).header("Location", originalUrl).build()
                : ResponseEntity.notFound().build();
    }
}
