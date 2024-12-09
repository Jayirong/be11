package com.yummy.be11.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yummy.be11.model.Recipe;
import com.yummy.be11.service.RecipeService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addRecipe(@PathVariable Long userId, @RequestBody Recipe recipe) {
        try {
            if (recipe.getNombre() == null || recipe.getDescripcion() == null) {
                return new ResponseEntity<>(Map.of("error", "Datos inválidos"), HttpStatus.BAD_REQUEST);
            }
            Recipe createdRecipe = recipeService.createRecipe(userId, recipe);
            return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    
    
    @GetMapping()
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId) {
        try {
            Recipe recipe = recipeService.getRecipeById(recipeId);
            return new ResponseEntity<>(recipe, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<?> updateEntity(@PathVariable Long recipeId, @RequestBody Recipe updatedRecipe) {
        try {
            Recipe recipe = recipeService.updateRecipe(recipeId, updatedRecipe);
            return new ResponseEntity<>(recipe, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeId) {
        try {
            recipeService.deleteRecipeById(recipeId);
            return new ResponseEntity<>("Recipe deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}
