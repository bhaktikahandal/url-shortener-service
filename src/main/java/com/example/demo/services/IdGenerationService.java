package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGenerationService {
    public String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
