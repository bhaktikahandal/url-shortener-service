package com.example.demo.controllers.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlRequest {
        private String originalUrl;
        private String customUrl;
        private long userId;
    }
