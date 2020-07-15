package com.bks.recipe.util

import androidx.fragment.app.Fragment
import com.bks.recipe.RecipeApplication

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as RecipeApplication).recipeRepository
    return ViewModelFactory(repository, this)
}
