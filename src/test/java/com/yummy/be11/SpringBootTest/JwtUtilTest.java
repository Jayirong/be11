package com.yummy.be11.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.yummy.be11.security.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    private String testSecretKey;

    @BeforeEach
    void setUp() {
        // Usamos una clave secreta de prueba para este test
        testSecretKey = "EstaEsUnaClaveMuyLargaYSeguraParaTesting123456789";
        jwtUtil = new JwtUtil(testSecretKey);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token, "El token no debería ser nulo");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(testSecretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject(), "El nombre de usuario debería coincidir");
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token, username), "El token debería ser válido para el usuario proporcionado");
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        String username = "testuser";

        JwtUtil shortLivedJwtUtil = new JwtUtil(testSecretKey) {
            @Override
            public String generateToken(String username) {
                return Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 1000)) // 1 segundo de vida
                        .signWith(Keys.hmacShaKeyFor(testSecretKey.getBytes()), SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String token = shortLivedJwtUtil.generateToken(username);
        // Esperar a que el token expire
        Thread.sleep(2000);

        assertFalse(jwtUtil.validateToken(token, username), "El token no debería ser válido porque está expirado");
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidUsername() {
        String validUsername = "testuser";
        String invalidUsername = "wronguser";
        String token = jwtUtil.generateToken(validUsername);

        assertFalse(jwtUtil.validateToken(token, invalidUsername), "El token no debería ser válido para un usuario incorrecto");
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername, "El nombre de usuario extraído debería coincidir con el original");
    }
}
