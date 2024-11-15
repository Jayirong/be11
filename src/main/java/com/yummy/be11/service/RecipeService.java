package com.yummy.be11.service;

import java.util.List;

import com.yummy.be11.model.Recipe;

public interface RecipeService {
    Recipe createRecipe(Long userId, Recipe recipe);
    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long recipeId);
    Recipe updateRecipe(Long recipeId, Recipe updatedRecipe);
    void deleteRecipeById(Long recipeId);
}
