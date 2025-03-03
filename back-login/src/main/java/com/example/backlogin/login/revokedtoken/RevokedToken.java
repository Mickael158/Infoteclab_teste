package com.example.backlogin.login.revokedtoken;

import jakarta.persistence.*;

import java.sql.Date;

// Classe pour gere la validiter du token
@Entity
public class RevokedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String accessToken;
    @Column(length = 2000)
    private String refreshToken;
    private Date revokedAt;

    public RevokedToken() {
    }

    public RevokedToken(String accessToken, String refreshToken) {
        this.setAccessToken(accessToken);
        this.setRefreshToken(refreshToken);
        this.setRevokedAt(new Date(new java.util.Date().getTime()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Date revokedAt) {
        this.revokedAt = revokedAt;
    }
}


