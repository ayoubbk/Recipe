package com.bks.recipe.adapters

interface OnRecipeListener {

    fun onRecipeClick(position: Int)

    fun onCategoryClick(category: String)
}