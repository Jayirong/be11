package com.yummy.be11.WebMvcTest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yummy.be11.controller.RecipeController;
import com.yummy.be11.model.Recipe;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.CustomUserDetailsService;
import com.yummy.be11.service.RecipeService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAllRecipes_ShouldReturnAllRecipes() throws Exception {
        // Arrange
        Recipe recipe1 = new Recipe();
        recipe1.setId_recipe(1L);
        recipe1.setNombre("Receta 1");
        recipe1.setDescripcion("Descripción 1");
        recipe1.setTipo_cocina(1L);
        recipe1.setPais_origen(1L);
        recipe1.setDificultad(1L);

        Recipe recipe2 = new Recipe();
        recipe2.setId_recipe(2L);
        recipe2.setNombre("Receta 2");
        recipe2.setDescripcion("Descripción 2");
        recipe2.setTipo_cocina(2L);
        recipe2.setPais_origen(2L);
        recipe2.setDificultad(2L);

        List<Recipe> recipes = Arrays.asList(recipe1, recipe2);

        Mockito.when(recipeService.getAllRecipes()).thenReturn(recipes);

        // Act & Assert
        mockMvc.perform(get("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id_recipe", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Receta 1")))
                .andExpect(jsonPath("$[1].id_recipe", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Receta 2")));
    }

    @Test
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists() throws Exception {
        // Arrange
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId_recipe(recipeId);
        recipe.setNombre("Receta 1");
        recipe.setDescripcion("Descripción 1");
        recipe.setTipo_cocina(1L);
        recipe.setPais_origen(1L);
        recipe.setDificultad(1L);

        Mockito.when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_recipe", is(recipeId.intValue())))
                .andExpect(jsonPath("$.nombre", is("Receta 1")))
                .andExpect(jsonPath("$.descripcion", is("Descripción 1")));
    }

    @Test
    void getRecipeById_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        // Arrange
        Long recipeId = 99L;
        Mockito.when(recipeService.getRecipeById(recipeId))
                .thenThrow(new RuntimeException("Receta no encontrada"));

        // Act & Assert
        mockMvc.perform(get("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Receta no encontrada")));
    }

    @Test
    void addRecipe_ShouldCreateRecipe_WhenDataIsValid() throws Exception {
        // Arrange
        Long userId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId_recipe(1L);
        recipe.setNombre("Receta Válida");
        recipe.setDescripcion("Descripción válida");
        recipe.setTipo_cocina(1L);
        recipe.setPais_origen(1L);
        recipe.setDificultad(1L);

        Mockito.when(recipeService.createRecipe(userId, recipe)).thenReturn(recipe);

        // Act & Assert
        mockMvc.perform(post("/api/recipes/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_recipe").value(1L))
                .andExpect(jsonPath("$.nombre").value("Receta Válida"))
                .andExpect(jsonPath("$.descripcion").value("Descripción válida"));
    }

    @Test
    void addRecipe_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        // Arrange
        Long userId = 1L;
        Recipe invalidRecipe = new Recipe(); // Datos vacíos o inválidos

        // Act & Assert
        mockMvc.perform(post("/api/recipes/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRecipe)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipe_ShouldUpdateRecipe_WhenRecipeExists() throws Exception {
        // Arrange
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId_recipe(1L);
        existingRecipe.setNombre("Receta Actualizada");
        existingRecipe.setDescripcion("Descripción Actualizada");
        existingRecipe.setTipo_cocina(2L);
        existingRecipe.setPais_origen(2L);
        existingRecipe.setDificultad(2L);

        Mockito.when(recipeService.updateRecipe(eq(1L), any(Recipe.class))).thenReturn(existingRecipe);

        // Act & Assert
        mockMvc.perform(put("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nombre": "Receta Actualizada",
                            "descripcion": "Descripción Actualizada",
                            "tipo_cocina": 2,
                            "pais_origen": 2,
                            "dificultad": 2
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_recipe").value(1))
                .andExpect(jsonPath("$.nombre").value("Receta Actualizada"))
                .andExpect(jsonPath("$.descripcion").value("Descripción Actualizada"));
    }

    @Test
    void updateRecipe_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(recipeService.updateRecipe(eq(999L), any(Recipe.class)))
                .thenThrow(new RuntimeException("Receta no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api/recipes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nombre": "Receta Nueva",
                            "descripcion": "Descripción Nueva",
                            "tipo_cocina": 2,
                            "pais_origen": 2,
                            "dificultad": 2
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Receta no encontrada"));
    }

    @Test
    void deleteRecipe_ShouldDelete_WhenRecipeExists() throws Exception {
        // Arrange
        Long recipeId = 1L;
        doNothing().when(recipeService).deleteRecipeById(recipeId);

        // Act & Assert
        mockMvc.perform(delete("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe deleted successfully"));
    }

    @Test
    void deleteRecipe_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        // Arrange
        Long recipeId = 999L;
        doThrow(new RuntimeException("Receta no encontrada")).when(recipeService).deleteRecipeById(recipeId);

        // Act & Assert
        mockMvc.perform(delete("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Receta no encontrada"));
    }
}
