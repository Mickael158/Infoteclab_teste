package com.example.backlogin.login;

import com.example.backlogin.config.JWTManager;
import com.example.backlogin.login.revokedtoken.RevokedToken;
import com.example.backlogin.login.revokedtoken.RevokedTokenRepository;
import com.example.backlogin.user.User;
import com.example.backlogin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;


// Controller gestion d'utilisateur
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTManager jwtManager;
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    // Controller de login
    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody UserDTO userDTO) {
        try {
            // Verifier l'utilsateur par mail et par mot de passe (hasher)
            User user = userService.getUserByMailAndPassword(userDTO.getMail(), userDTO.getPassword());
            return ResponseEntity.ok(generateTokenResponse(user));
        } catch (Exception e) {
            // Si il y a un erreur de donne
            return ResponseEntity.status(401).body(createErrorResponse("Invalid credentials"));
        }
    }
    // Nouveau Utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            // Enregitrer nouveau utilisateur et cripter son mot de passe
            User user = new User(userDTO.getUsername(), userDTO.getMail(), userDTO.getPassword(), userDTO.getRole());
            user = userService.saveUser(user);
            return ResponseEntity.ok(generateTokenResponse(user));
        } catch (Exception e) {
            // Si il y a un erreur de donne
            return ResponseEntity.status(401).body(createErrorResponse("Invalid credentials"));
        }
    }
    // Mettre a jour le token
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody HashMap<String, String> body) {
        String refreshToken = body.get("refreshToken");
        // Si refreshToken est vide ou null => Erreur 400
        if (!StringUtils.hasText(refreshToken)) {
            return ResponseEntity.status(400).body(createErrorResponse("Refresh token is missing"));
        }

        try {
            // Si le refresh token est encore valide, sinon lève une exception
            jwtManager.validateToken(refreshToken);
            // Prend le UserName dans refresh token
            String username = jwtManager.getUsername(refreshToken);
            // Select user By username
            User user = userService.getUserByUserName(username);
            // Genere et renvoyer le nouveau access token
            return ResponseEntity.ok(createTokenResponse(jwtManager.generateAccessToken(user), null));
        } catch (AuthenticationCredentialsNotFoundException e) {
            // Si refresh token est invalide
            return ResponseEntity.status(401).body(createErrorResponse("Invalid or expired refresh token"));
        } catch (Exception e) {
            // Si il y a un erreur interne
            return ResponseEntity.status(500).body(createErrorResponse("Error refreshing token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody HashMap<String, String> body) {
        String accessToken = body.get("access");
        String refreshToken = body.get("refresh");

        if (!StringUtils.hasText(accessToken) || !StringUtils.hasText(refreshToken)) {
            return ResponseEntity.badRequest().body(createErrorResponse("Missing access or refresh token"));
        }

        revokedTokenRepository.save(new RevokedToken(accessToken, refreshToken));
        return ResponseEntity.ok(createSuccessResponse("Logged out successfully"));
    }

    // Mandefa le Token
    private HashMap<String, String> generateTokenResponse(User user) {
        return createTokenResponse(
                jwtManager.generateAccessToken(user),
                jwtManager.generateRefreshToken(user)
        );
    }

    // Mise a jour token
    private HashMap<String, String> createTokenResponse(String accessToken, String refreshToken) {
        HashMap<String, String> response = new HashMap<>();
        response.put("dateTime", new Date().toString());
        response.put("accessToken", accessToken);
        if (refreshToken != null) {
            response.put("refreshToken", refreshToken);
        }
        return response;
    }

    // Creation de message d'erreur
    private HashMap<String, String> createErrorResponse(String message) {
        HashMap<String, String> response = new HashMap<>();
        response.put("dateTime", new Date().toString());
        response.put("error", message);
        return response;
    }

    // Creation de message succe
    private HashMap<String, String> createSuccessResponse(String message) {
        HashMap<String, String> response = new HashMap<>();
        response.put("dateTime", new Date().toString());
        response.put("message", message);
        return response;
    }
}
