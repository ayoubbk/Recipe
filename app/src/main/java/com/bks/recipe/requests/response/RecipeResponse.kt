package com.bks.recipe.requests.response

import com.bks.recipe.models.Recipe

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class RecipeResponse (
    @SerializedName("recipe")
    @Expose
    val recipe: Recipe? = null,
    @SerializedName("error")
    @Expose
    val error: String? = null

)