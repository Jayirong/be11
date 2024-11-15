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
    public Recipe createRecipe(Long userId, Recipe recipe) {
        logger.info("createRecipe llamado con userName: {}", userId);

        //obtencion de user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            logger.error("Usuario no encontrado con username: {}", userId);
            throw new RuntimeException("Usuario no encontrado :P");
        }

        recipe.setIdUser(userId);
        return recipeRepository.save(recipe);
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

    @Override
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        recipe.setNombre(updatedRecipe.getNombre());
        recipe.setDescripcion(updatedRecipe.getDescripcion());
        recipe.setTipo_cocina(updatedRecipe.getTipo_cocina());
        recipe.setPais_origen(updatedRecipe.getPais_origen());
        recipe.setDificultad(updatedRecipe.getDificultad());
        recipe.setImg_ruta(updatedRecipe.getImg_ruta());

        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipeById(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RuntimeException("Receta no encontrada");
        }
        recipeRepository.deleteById(recipeId);
    }
    
}
