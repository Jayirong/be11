// package com.yummy.be11.SpringBootTest;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import com.yummy.be11.controller.AuthController;
// import com.yummy.be11.controller.RecipeController;
// import com.yummy.be11.model.Recipe;
// import com.yummy.be11.service.RecipeService;
// import com.yummy.be11.service.UserService;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.LocalDateTime;

// @WebMvcTest({RecipeController.class, AuthController.class})
// @AutoConfigureMockMvc
// class RecipeControllerRoleTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private RecipeService recipeService;

//     @MockBean
//     private UserService userService;

//     private final String updatedRecipeJson = """
//         {
//             "nombre": "Updated Recipe",
//             "descripcion": "An updated delicious recipe",
//             "tipo_cocina": 2,
//             "pais_origen": 1,
//             "dificultad": 3,
//             "idUser": 1
//         }
//     """;

//     @BeforeEach
//     void setup() {
//         // Configurar un mock para simular RecipeService
//         Recipe mockRecipe = new Recipe();
//         mockRecipe.setId_recipe(1L);
//         mockRecipe.setNombre("Original Recipe");
//         mockRecipe.setDescripcion("Original Description");
//         mockRecipe.setTipo_cocina(1L);
//         mockRecipe.setPais_origen(1L);
//         mockRecipe.setDificultad(2L);
//         mockRecipe.setIdUser(1L);
//         mockRecipe.setFecha_creacion(LocalDateTime.now());

//         // Simula el método updateRecipe
//         Mockito.when(recipeService.updateRecipe(Mockito.anyLong(), Mockito.any())).thenReturn(mockRecipe);
//         Mockito.doNothing().when(recipeService).deleteRecipeById(Mockito.anyLong());
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"USER"})
//     void shouldAllowAccess_WhenUserIsUser() throws Exception {
//         mockMvc.perform(put("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updatedRecipeJson))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(username = "admin", roles = {"ADMIN"})
//     void shouldAllowAccess_WhenUserIsAdmin() throws Exception {
//         mockMvc.perform(put("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updatedRecipeJson))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void shouldDenyAccess_WhenUserIsUnauthorized() throws Exception {
//         mockMvc.perform(put("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updatedRecipeJson))
//                 .andExpect(status().isUnauthorized());
//     }

//     @Test
//     void shouldAllowAnonymousAccessToGetRecipes() throws Exception {
//         // Realiza una solicitud GET al endpoint de recetas
//         mockMvc.perform(get("/api/recipes")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void shouldAllowAnonymousAccessToGetRecipeById() throws Exception {
//         // Realiza una solicitud GET al endpoint de recetas por ID
//         mockMvc.perform(get("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"USER"})
//     void shouldAllowAccess_WhenUserIsUser_ToDelete() throws Exception {
//         mockMvc.perform(delete("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(username = "admin", roles = {"ADMIN"})
//     void shouldAllowAccess_WhenUserIsAdmin_ToDelete() throws Exception {
//         mockMvc.perform(delete("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void shouldDenyAccess_WhenUserIsUnauthorized_ToDelete() throws Exception {
//         mockMvc.perform(delete("/api/recipes/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isUnauthorized());
//     }

//     @Test
//     void shouldAllowAnonymousAccessToLogin() throws Exception {
//         String loginRequestJson = """
//             {
//                 "username": "user",
//                 "password": "password"
//             }
//         """;

//         Mockito.when(userService.authenticateAndGenerateToken(Mockito.anyString(), Mockito.anyString()))
//             .thenReturn("dummy.jwt.token");

//         mockMvc.perform(post("/api/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(loginRequestJson))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.token").value("dummy.jwt.token"));
//     }
// }
