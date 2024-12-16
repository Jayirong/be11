package com.yummy.be11.WebMvcTest;

import com.yummy.be11.controller.AdminController;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.CustomUserDetailsService;
import com.yummy.be11.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
class AdminControllerRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario con rol ADMIN
    void shouldAllowAccess_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Verifica que el acceso es permitido
    }

    @Test
    void shouldDenyAccess_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Verifica que el acceso es denegado
    }
}