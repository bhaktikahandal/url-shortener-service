package com.example.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class AppException extends RuntimeException {
    private final String errorMessage;
    private final String identifier;
    private final long errorCode;
}
