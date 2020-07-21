package com.bks.recipe.util

class Constant {

    companion object {
        const val BASE_URL = "https://recipesapi.herokuapp.com"
        const val CONNECTION_TIMEOUT = 10L // 10 sec
        const val READ_TIMEOUT = 2L // 2 sec
        const val WRITE_TIMEOUT = 2L // 2 sec
        const val API_KEY  = ""
        const val QUERY_EXHAUSTED = "No more results."

        const val RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30 // 30 days (in seconds)

        val DEFAULT_SEARCH_CATEGORIES = arrayOf(
            "Barbeque",
            "Breakfast",
            "Chicken",
            "Beef",
            "Brunch",
            "Dinner",
            "Wine",
            "Italian"
        )

        val DEFAULT_SEARCH_CATEGORY_IMAGES = arrayOf(
            "barbeque",
            "breakfast",
            "chicken",
            "beef",
            "brunch",
            "dinner",
            "wine",
            "italian"
        )
    }
}