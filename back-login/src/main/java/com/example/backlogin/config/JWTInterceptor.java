package com.example.backlogin.config;

import com.example.backlogin.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTInterceptor extends OncePerRequestFilter {

    @Autowired
    private JWTManager jwt;

    @Autowired
    UserService userService;

    // Ajouter un filtre au requet
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Prendre l'access token dans l'objet
        String token = this.getJWTFromRequest(request);
        // Verifier si le token existe
        if (StringUtils.hasText(token)) {
            // Si le token valide
            try {
                jwt.validateToken(token);
                // avoir l'UserName dans le token
                String userName = jwt.getUsername(token);
                // Creation de l'objet User detail a partir de Username
                UserDetails userDetails = userService.getUserDetailsByUserName(userName);
                // Creation de l'objet Auth Token pour la securite Spring
                UsernamePasswordAuthenticationToken
                        authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // Ajouter l'objet de securite dans le contexte d'authentification
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // Si le token n'est pas valide
            catch (Exception e) {
                logger.error("AuthenticationCredentialsNotFoundException: " + e.getMessage());

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new ObjectMapper().writeValueAsString("Authentication failed:" +e.getMessage()));
                return;
            }
        }
        // Ajouter le filtre
        filterChain.doFilter(request, response);
    }

    // Maka Token ao anaty Header de la requet
    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
