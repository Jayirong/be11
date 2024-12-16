package com.yummy.be11.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.yummy.be11.controller.AdminController;
import com.yummy.be11.model.User;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.CustomUserDetailsService;
import com.yummy.be11.service.UserService;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers_ShouldListOfUsers_WhenCalledByAdmin() throws Exception {
        //arrange
        User user1 = new User();
        user1.setId_user(1L);
        user1.setUsername("user1");
        user1.setNombre("User");
        user1.setApellido("One");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setId_user(2L);
        user2.setUsername("user2");
        user2.setNombre("User");
        user2.setApellido("Two");
        user2.setPassword("password2");

        List<User> users = Arrays.asList(user1, user2);
        Mockito.when(userService.findAllUsers()).thenReturn(users);

        //act y assert
        mockMvc.perform(get("/api/admin/users")
                            .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id_user").value(1L))
                        .andExpect(jsonPath("$[0].username").value("user1"))
                        .andExpect(jsonPath("$[1].id_user").value(2L))
                        .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void deleteUser_ShouldReturnOk_WhenUserExists() throws Exception {
        // Arrange
        Long userId = 1L;
        Mockito.doNothing().when(userService).deleteUserById(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/user/delete/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 1L;
        Mockito.doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteUserById(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/user/delete/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void getUserByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        String username = "testuser";
        User mockUser = new User();
        mockUser.setId_user(1L);
        mockUser.setUsername(username);
        mockUser.setNombre("John");
        mockUser.setApellido("Doe");
        mockUser.setPassword("encryptedPassword");

        Mockito.when(userService.findByUsername(username)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/admin/user/{uname}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_user").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.nombre").value("John"))
                .andExpect(jsonPath("$.apellido").value("Doe"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void getUserByUsername_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        String username = "nonexistentuser";
        Mockito.when(userService.findByUsername(username))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/admin/user/{uname}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void updateUser_ShouldUpdateUser_WhenDataIsValid() throws Exception {
        // Arrange
        String username = "testuser";
        User updatedUser = new User();
        updatedUser.setId_user(1L);
        updatedUser.setUsername(username);
        updatedUser.setNombre("Updated");
        updatedUser.setApellido("User");
        updatedUser.setPassword("newpassword");

        Mockito.when(userService.updateUser(Mockito.eq(username), Mockito.any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/admin/user/update/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Updated\", \"apellido\":\"User\", \"password\":\"newpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Updated"))
                .andExpect(jsonPath("$.apellido").value("User"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado con rol ADMIN
    void updateUser_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        // Arrange
        String username = "testuser";

        Mockito.when(userService.updateUser(Mockito.eq(username), Mockito.any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(put("/api/admin/user/update/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\", \"apellido\":\"\", \"password\":\"\"}")) // Datos inválidos
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid data"));
    }

}
