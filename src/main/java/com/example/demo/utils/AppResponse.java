package com.example.demo.utils;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class AppResponse {
    private final String data;
    private final String error;
}
