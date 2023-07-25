package com.example.demo.controllers;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public CompletableFuture<ResponseEntity<String>> shortenUrl(@RequestBody UrlRequest urlRequest) {
        return urlService.shortenUrl(urlRequest).thenApply(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }

    @GetMapping("/{shortUrl}")
    public CompletableFuture<ResponseEntity<String>> getOriginalUrl(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl)
                .thenApply(result -> ResponseEntity.status(HttpStatus.FOUND).header("Location", result).build());

    }
}
