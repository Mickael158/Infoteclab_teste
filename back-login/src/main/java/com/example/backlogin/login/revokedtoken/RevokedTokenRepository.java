package com.example.backlogin.login.revokedtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByAccessToken(String accessToken);
    boolean existsByRefreshToken(String refreshToken);
}
