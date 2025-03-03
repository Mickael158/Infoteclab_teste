package com.example.backlogin.config;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import com.example.backlogin.login.revokedtoken.RevokedTokenRepository;
import com.example.backlogin.user.User;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTManager {

    // Durer de la validete de refresh token en jour
    public static final int validateRefreshToken = 30; // day

    // Durer de la validete de access token en munite
    public static final int validateAccessToken = 15; // minute

    // Charger l'envirennement
    private static final Dotenv dotenv = Dotenv.load();

    // Avoir la cles d'encryption du Token
    private static final String secret = dotenv.get("APP_SECRET_KEY"); // .env

    // EN coder la cles
    private static final Key key = new SecretKeySpec(Base64.getDecoder().decode(secret),
            SignatureAlgorithm.HS256.getJcaName());
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    // Generer le token d'access
    public String generateAccessToken(User user) {
        Date currentDate = new Date();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + minuteToMs(validateAccessToken)))
                .claim("user_id", user.getId())
                .claim("password", user.getPassword())
                .signWith(key)
                .compact();
    }

    // Generer le token de rafrechissement
    public String generateRefreshToken(User user) {
        Date currentDate = new Date();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + daysToMs(validateRefreshToken)))
                .signWith(key)
                .compact();
    }

    // Avoir le UserNAme dans Token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Verifier si la token est valide
    public void validateToken(String token) throws AuthenticationCredentialsNotFoundException {
        // Si le token n'est pas encore blacklister
        if (revokedTokenRepository.existsByRefreshToken(token) || revokedTokenRepository.existsByAccessToken(token)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token");
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token",
                    ex.fillInStackTrace());
        }
    }

    public static int minuteToMs(int min) {
        return min * 60 * 60 * 1000;
    }
    public static int daysToMs(int days) {
        return days * 24 * 60 * 60 * 1000;
    }

}