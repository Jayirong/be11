package com.yummy.be11.service;

import java.util.List;

import com.yummy.be11.model.Recipe;

public interface RecipeService {
    Recipe createRecipe(String userUname, Recipe recipe);
    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long recipeId);
}
