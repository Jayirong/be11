package com.yummy.be11.testSecurity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertNotNull(token, "El token no debería ser nulo");

        // Decodificar el token para verificar su validez
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(testSecretKey.getBytes()) // Usamos la misma clave que configuramos en el setup
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject(), "El nombre de usuario debería coincidir");
    }

     @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act & Assert
        assertTrue(jwtUtil.validateToken(token, username), "El token debería ser válido para el usuario proporcionado");
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // Arrange
        String username = "testuser";

        // Modificamos la expiración del token para pruebas cortas
        JwtUtil shortLivedJwtUtil = new JwtUtil(testSecretKey) {
            @Override
            public String generateToken(String username) {
                return Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 1000)) // 1 segundo
                        .signWith(Keys.hmacShaKeyFor(testSecretKey.getBytes()), SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String token = shortLivedJwtUtil.generateToken(username);

        // Esperamos que el token expire
        Thread.sleep(2000);

        // Act & Assert
        assertFalse(jwtUtil.validateToken(token, username), "El token no debería ser válido porque está expirado");
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidUsername() {
        // Arrange
        String validUsername = "testuser";
        String invalidUsername = "wronguser";
        String token = jwtUtil.generateToken(validUsername);

        // Act & Assert
        assertFalse(jwtUtil.validateToken(token, invalidUsername), "El token no debería ser válido para un usuario incorrecto");
    }


    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username); // Generar un token válido

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername, "El nombre de usuario extraído debería coincidir con el original");
    }
}
