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
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/user/{userUname}")
    public ResponseEntity<Recipe> addRecipe(@PathVariable String userUname, @RequestBody Recipe recipe) {
        Recipe createdRecipe = recipeService.createRecipe(userUname, recipe);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }    
    
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getREcipeById(@PathVariable Long recipeId) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }
    
    

}
