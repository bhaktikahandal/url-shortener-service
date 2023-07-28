package com.example.demo.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdGenerationServiceTest {

    @Test
    void testGenerateId() {
        IdGenerationService idGenerationService = new IdGenerationService();

        String generatedId = idGenerationService.generateId();

        assertNotNull(generatedId);
        assertFalse(generatedId.isEmpty());
    }
}
