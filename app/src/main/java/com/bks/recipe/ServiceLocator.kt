package com.bks.recipe

import android.content.Context
import com.bks.recipe.repositories.RecipeRepository

object ServiceLocator  {

    @Volatile
    var recipeRepository: RecipeRepository? = null

    fun provideRecipeRepository(context: Context): RecipeRepository {
        synchronized(this) {
            return recipeRepository ?: recipeRepository ?: createRecipeRepository(context)
        }
    }

    private fun createRecipeRepository(context: Context): RecipeRepository {
        return RecipeRepository.getInstance(context)
    }



}