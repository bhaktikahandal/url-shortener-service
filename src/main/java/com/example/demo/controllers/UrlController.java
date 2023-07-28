package com.example.demo.controllers;

import com.example.demo.controllers.requests.UrlRequest;
import com.example.demo.services.UrlService;
import com.example.demo.utils.AppException;
import com.example.demo.utils.AppResponse;
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

    @PostMapping(path = "/shorten", produces = "application/json")
    public CompletableFuture<ResponseEntity<AppResponse>> shortenUrl(@RequestBody UrlRequest urlRequest) {
        try {
            return urlService.shortenUrl(urlRequest).thenApply(result -> new ResponseEntity<>(AppResponse.builder().data(result).build(), HttpStatus.CREATED));
        } catch (AppException appException) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(AppResponse.builder().error(appException.toString()).build()
                    , HttpStatus.BAD_REQUEST));
        } catch (Exception exception) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(AppResponse.builder().error(exception.getMessage()).build()
                    , HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{shortUrl}")
    public CompletableFuture<ResponseEntity<String>> getOriginalUrl(@PathVariable String shortUrl) {
        try {
            return urlService.getOriginalUrl(shortUrl)
                    .thenApply(result -> ResponseEntity.status(HttpStatus.FOUND).header("Location", result).build());
        } catch (AppException appException) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception exception) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @DeleteMapping("/clean")
    public ResponseEntity<String> cleanExpiredUrls() {
        urlService.cleanExpiredUrls();
        return ResponseEntity.ok("Expired URLs deleted successfully.");
    }

}
