package com.bks.recipe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bks.recipe.models.Recipe
import com.bks.recipe.repositories.RecipeRepository
import com.bks.recipe.util.Resource

class RecipeViewModel constructor(private val recipeRepository : RecipeRepository) : ViewModel() {



    fun searchRecipeApi(recipeId : String) : LiveData<Resource<Recipe>> {
        return recipeRepository.searchRecipeApi(recipeId)
    }


}