package com.example.demo.controllers.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlRequest {
        private String originalUrl;
        private String customUrl;
        private LocalDateTime expirationDate;
    }
