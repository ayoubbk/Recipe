package com.bks.recipe.util

import androidx.fragment.app.FragmentActivity
import com.bks.recipe.ServiceLocator

fun FragmentActivity.getViewModelFactory(): ViewModelFactory {
    val repository = ServiceLocator.provideRecipeRepository(this)
    return ViewModelFactory(repository, this)
}