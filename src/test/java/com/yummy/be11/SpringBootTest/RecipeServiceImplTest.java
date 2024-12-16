package com.yummy.be11.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.yummy.be11.model.Recipe;
import com.yummy.be11.model.User;
import com.yummy.be11.repository.RecipeRepository;
import com.yummy.be11.repository.UserRepository;
import com.yummy.be11.service.RecipeServiceImpl;

class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRecipe_ShouldCreateRecipe_WhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId_user(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Recipe recipe = new Recipe();
        recipe.setNombre("Receta de prea");
        recipe.setDescripcion("Descripciion de prea");

        when(recipeRepository.save(recipe)).thenReturn(recipe);

        Recipe result = recipeService.createRecipe(userId, recipe);

        assertNotNull(result);
        assertEquals("Receta de prea", result.getNombre());
        assertEquals(userId, result.getIdUser());

        verify(userRepository, times(1)).findById(userId);
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    void createRecipe_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Recipe recipe = new Recipe();
        recipe.setNombre("Receta de prueba");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.createRecipe(userId, recipe);
        });

        assertEquals("Usuario no encontrado :P", exception.getMessage());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void getAllRecipes_ShouldReturnListOfRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setId_recipe(1L);
        recipe1.setNombre("Receta 1");
        recipe1.setDescripcion("Descripcion 1");

        Recipe recipe2 = new Recipe();
        recipe2.setId_recipe(2L);
        recipe2.setNombre("Receta 2");
        recipe2.setDescripcion("Descripcion 2");

        List<Recipe> mockRecipes = Arrays.asList(recipe1, recipe2);

        when(recipeRepository.findAll()).thenReturn(mockRecipes);

        List<Recipe> result = recipeService.getAllRecipes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Receta 1", result.get(0).getNombre());
        assertEquals("Receta 2", result.get(1).getNombre());

        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists() {
        Recipe mockRecipe = new Recipe();
        mockRecipe.setId_recipe(1L);
        mockRecipe.setNombre("Receta 1");
        mockRecipe.setDescripcion("Descripción de la receta");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));

        Recipe result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId_recipe());
        assertEquals("Receta 1", result.getNombre());
        assertEquals("Descripción de la receta", result.getDescripcion());

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    void getRecipeById_ShouldThrowException_WhenRecipeDoesNotExist() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.getRecipeById(1L);
        });

        assertEquals("Receta no pillada >:P", exception.getMessage());
        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    void updateRecipe_ShouldUpdateFields_WhenRecipeExists() {
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId_recipe(1L);
        existingRecipe.setNombre("Receta Original");
        existingRecipe.setDescripcion("Descripción Original");
        existingRecipe.setTipo_cocina(1L);
        existingRecipe.setPais_origen(1L);
        existingRecipe.setDificultad(1L);
        existingRecipe.setImg_ruta("ruta/original.jpg");

        Recipe updatedRecipe = new Recipe();
        updatedRecipe.setNombre("Receta Actualizada");
        updatedRecipe.setDescripcion("Descripción Actualizada");
        updatedRecipe.setTipo_cocina(2L);
        updatedRecipe.setPais_origen(2L);
        updatedRecipe.setDificultad(2L);
        updatedRecipe.setImg_ruta("ruta/actualizada.jpg");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.updateRecipe(1L, updatedRecipe);

        assertNotNull(result);
        assertEquals("Receta Actualizada", result.getNombre());
        assertEquals("Descripción Actualizada", result.getDescripcion());
        assertEquals(2L, result.getTipo_cocina());
        assertEquals(2L, result.getPais_origen());
        assertEquals(2L, result.getDificultad());
        assertEquals("ruta/actualizada.jpg", result.getImg_ruta());

        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).save(existingRecipe);
    }

    @Test
    void updateRecipe_ShouldThrowException_WhenRecipeDoesNotExist() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.updateRecipe(1L, new Recipe());
        });

        assertEquals("Receta no encontrada", exception.getMessage());
        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void deleteRecipeById_ShouldDeleteRecipe_WhenRecipeExists() {
        Long recipeId = 1L;
        when(recipeRepository.existsById(recipeId)).thenReturn(true);

        recipeService.deleteRecipeById(recipeId);

        verify(recipeRepository, times(1)).existsById(recipeId);
        verify(recipeRepository, times(1)).deleteById(recipeId);
    }

    @Test
    void deleteRecipeById_ShouldThrowException_WhenRecipeDoesNotExist() {
        Long recipeId = 1L;
        when(recipeRepository.existsById(recipeId)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.deleteRecipeById(recipeId);
        });

        assertEquals("Receta no encontrada", exception.getMessage());
        verify(recipeRepository, times(1)).existsById(recipeId);
        verify(recipeRepository, never()).deleteById(recipeId);
    }
}
