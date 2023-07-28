package com.example.demo.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    private Timestamp createdAt;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "userEntity")
    private Set<UrlEntity> urlEntitySet = new HashSet<>();
}
