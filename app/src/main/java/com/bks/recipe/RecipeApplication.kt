package com.bks.recipe

import android.app.Application
import com.bks.recipe.repositories.RecipeRepository

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */

class RecipeApplication : Application() {

    // Depends on the flavor,
    val recipeRepository: RecipeRepository
        get() = ServiceLocator.provideRecipeRepository(this)


    override fun onCreate() {
        super.onCreate()
    }
}