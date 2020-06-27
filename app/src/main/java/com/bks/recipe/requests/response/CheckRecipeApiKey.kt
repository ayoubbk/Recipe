package com.bks.recipe.requests.response

object CheckRecipeApiKey {
    internal fun isRecipeApiKeyValid(response: RecipeSearchResponse): Boolean {
        return response.error == null
    }

    internal fun isRecipeApiKeyValid(response: RecipeResponse): Boolean {
        return response.error == null
    }
}