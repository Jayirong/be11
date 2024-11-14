package com.yummy.be11.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yummy.be11.model.Recipe;
import com.yummy.be11.model.User;
import com.yummy.be11.repository.RecipeRepository;
import com.yummy.be11.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeServiceImpl.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Recipe createRecipe(String userUname, Recipe recipe) {
        logger.info("createRecipe llamado con userName: {}", userUname);

        //obtencion de user
        Optional<User> userOptional = userRepository.findByUsername(userUname);
        if (userOptional.isEmpty()) {
            logger.error("Usuario no encontrado con username: {}", userUname);
            throw new RuntimeException("Usuario no encontrado :P");
        }
        User user = userOptional.get();
        recipe.setUser(user);

        Recipe savedRecipe = recipeRepository.save(recipe);
        logger.info("Receta wardada exitosamente con ID: {}", savedRecipe.getId_recipe());

        return savedRecipe;
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no pillada >:P"));
    }
}
