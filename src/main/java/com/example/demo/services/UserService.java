package com.example.demo.services;

import com.example.demo.models.UserEntity;
import com.example.demo.repositories.UserRepository;
import com.example.demo.utils.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity fetchByUserId(long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        log.error("user not found userId: {}", userId);
        throw new AppException("Invalid userId ", String.valueOf(userId), 104);
    }
}
