package com.example.demo.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "urls")
@Entity
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    private Timestamp createdAt;

    private Timestamp expirationTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public UrlEntity(String originalUrl, String shortUrl, Timestamp createdAt, Timestamp expirationTime, UserEntity userEntity) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.expirationTime = expirationTime;
        this.userEntity = userEntity;
    }
}