package com.bks.recipe.requests.response

import com.bks.recipe.models.Recipe
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RecipeSearchResponse(
    @SerializedName("count")
    @Expose
    val count: Int = 0,
    @SerializedName("recipes")
    @Expose
    val recipes: List<Recipe>? = null,
    @SerializedName("error")
    @Expose
    val error: String? = null

)