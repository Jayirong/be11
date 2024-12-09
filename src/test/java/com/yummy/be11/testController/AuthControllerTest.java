package com.yummy.be11.testController;

import com.yummy.be11.controller.AuthController;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.CustomUserDetailsService;
import com.yummy.be11.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar los filtros de seguridad
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil; // Mockear JwtUtil

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // Mockear CustomUserDetailsService

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String token = "validToken";

        Mockito.when(userService.authenticateAndGenerateToken(username, password)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"testpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        // Arrange
        Mockito.when(userService.authenticateAndGenerateToken(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invaliduser\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }
}
