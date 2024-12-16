package com.yummy.be11.WebMvcTest;

import com.yummy.be11.controller.UserController;
import com.yummy.be11.model.User;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.UserService;
import com.yummy.be11.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactivamos filtros de seguridad
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil; // Simulamos JwtUtil

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // Simulamos CustomUserDetailsService

    private final String validUserRegistrationJson = """
        {
            "username": "newuser",
            "password": "securepassword",
            "email": "newuser@example.com"
        }
    """;

    @Test
    void registerUser_ShouldCreateUser_WhenDataIsValid() throws Exception {
        // Arrange
        User validUser = new User();
        validUser.setId_user(1L);
        validUser.setUsername("testuser");
        validUser.setPassword("testpassword");

        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenReturn(validUser);

        // Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"testpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_user").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenDataIsIncomplete() throws Exception {
        // Arrange
        Mockito.when(userService.registerUser(Mockito.any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}")) // Datos incompletos
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid data"));
    }

    @Test
    void getUserDetails_ShouldReturnUserDetails_WhenUserIsAuthenticated() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId_user(1L);
        mockUser.setUsername("testuser");
        mockUser.setNombre("John");
        mockUser.setApellido("Doe");
        mockUser.setPassword("encryptedPassword");

        Mockito.when(userService.findByUsername("testuser")).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(() -> "testuser")) // Simula el Principal
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_user").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.nombre").value("John"))
                .andExpect(jsonPath("$.apellido").value("Doe"));
    }

    @Test
    void getUserDetails_ShouldReturnUnauthorized_WhenNoUserIsAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User is not authenticated"));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenDataIsValid() throws Exception {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");

        Mockito.when(userService.updateUser(Mockito.eq("authenticatedUser"), Mockito.any(User.class)))
                .thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/user/update")
                        .principal(() -> "authenticatedUser") // Simula usuario autenticado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updateduser\", \"password\":\"newpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void updateUser_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updateduser\", \"password\":\"newpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
        void updateUser_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        // Arrange
        Mockito.when(userService.updateUser(Mockito.anyString(), Mockito.any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(put("/api/user/update")
                        .principal(() -> "authenticatedUser") // Simula usuario autenticado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}")) // Datos inválidos
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid data"));
        }


    @Test
    void shouldAllowAnonymousAccessToRegister() throws Exception {
        // Realiza una solicitud POST al endpoint de registro
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserRegistrationJson))
                .andExpect(status().isOk()); // O `isCreated()` dependiendo de la implementación
    }

}
