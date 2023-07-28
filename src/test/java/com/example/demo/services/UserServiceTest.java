package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.example.demo.models.UserEntity;
import com.example.demo.repositories.UserRepository;
import com.example.demo.utils.AppException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchByUserIdWithExistingUser() {
        long userId = 123;
        UserEntity userEntity = UserEntity.builder().userId(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.fetchByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testFetchByUserIdWithNonExistingUser() {
        long userId = 123;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.fetchByUserId(userId));
    }
}