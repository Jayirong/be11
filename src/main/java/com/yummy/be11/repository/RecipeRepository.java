package com.yummy.be11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yummy.be11.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    
}
