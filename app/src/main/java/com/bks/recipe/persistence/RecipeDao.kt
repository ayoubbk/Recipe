package com.bks.recipe.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bks.recipe.models.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipes(recipes: List<Recipe>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Query("""UPDATE recipes SET title = :title, publisher = :publisher, image_url = :imageUrl, social_rank = :socialRank
                    WHERE recipe_id = :recipeId """)
    fun updateRecipe(recipeId: String, title: String, publisher: String, imageUrl: String, socialRank: Float)

    @Query(""" SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' 
                     OR ingredients LIKE '%' || :query || '%' ORDER BY social_rank DESC LIMIT (:pageNumber * 30)  """)
    fun searchRecipes(query: String, pageNumber: Int): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    fun getRecipeById(recipeId : String) : LiveData<Recipe>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipes(vararg recipe: Recipe): LongArray

}