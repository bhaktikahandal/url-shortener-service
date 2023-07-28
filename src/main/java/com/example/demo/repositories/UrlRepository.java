package com.example.demo.repositories;

import com.example.demo.models.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    UrlEntity findByShortUrl(String shortUrl);

    @Query(value = "SELECT * FROM urls u WHERE u.original_url = :originalUrl and u.user_id = :userId"
            , nativeQuery = true)
    UrlEntity findByOriginalUrlAndUserId(
            @Param("originalUrl") String originalUrl,
            @Param("userId") long userId);
    List<UrlEntity> findByExpirationTimeBefore(Timestamp currentTimestamp);
}
