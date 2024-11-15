package com.yummy.be11.controller;

import java.util.List;

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
    public ResponseEntity<Recipe> addRecipe(@PathVariable Long userId, @RequestBody Recipe recipe) {
        Recipe createdRecipe = recipeService.createRecipe(userId, recipe);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }    
    
    @GetMapping()
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getREcipeById(@PathVariable Long recipeId) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<Recipe> updateEntity(@PathVariable Long recipeId, @RequestBody Recipe updatedRecipe) {
        Recipe recipe = recipeService.updateRecipe(recipeId, updatedRecipe);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }
    
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipeById(recipeId);
        return new ResponseEntity<>("Recipe deleted successfully", HttpStatus.OK);
    }

}
