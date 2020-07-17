package com.bks.recipe.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.bks.recipe.executor.AppExecutors
import com.bks.recipe.models.Recipe
import com.bks.recipe.persistence.RecipeDao
import com.bks.recipe.persistence.RecipeDatabase
import com.bks.recipe.requests.ServiceGenerator
import com.bks.recipe.requests.response.ApiResponse
import com.bks.recipe.requests.response.RecipeResponse
import com.bks.recipe.requests.response.RecipeSearchResponse
import com.bks.recipe.util.Constant
import com.bks.recipe.util.NetworkBoundResource
import com.bks.recipe.util.Resource

class RecipeRepository(private val context: Context) {
    private val recipeDao: RecipeDao = RecipeDatabase.getInstance(context).getRecipeDao() // val mean readOnly

    fun searchRecipesApi(query: String, pageNumber: Int): LiveData<Resource<List<Recipe>>> {
        return object :
            NetworkBoundResource<List<Recipe>, RecipeSearchResponse?>(AppExecutors.getInstance()) {

            override fun saveCallResult(item: RecipeSearchResponse?) {
                item?.recipes?.let { it ->
                    val recipes = it.toTypedArray()
                    var index = 0
                    // *param : spread operator
                    recipeDao.insertRecipes(*recipes).forEach {rowId ->
                        if(rowId == -1L) {
                            Log.d(TAG, "saveCallResult : CONFLICT...This recipe is already in the cache")
                            // if the recipe already exist I don't want to set the ingredients or timestamp b/c
                            // they will be erased
                            recipeDao.updateRecipe(
                                recipes[index].recipe_id,
                                recipes[index].title,
                                recipes[index].publisher,
                                recipes[index].imageUrl,
                                recipes[index].socialRank
                            )
                        }
                    }
                }
            }

            override fun shouldFetch(data: List<Recipe>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Recipe>> {
                return recipeDao.searchRecipes(query, pageNumber)
            }

            // creating LiveData Retrofit object
            // LiveData is executed asynchronously , the retrofit call object will automatically be done on the
            // background thread, I won't use Executors.
            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse?>> {
                return ServiceGenerator.recipeApi.searchRecipe(Constant.API_KEY, query, pageNumber.toString())
            }
        }.asLiveData()
    }

    fun searchRecipeApi(recipeId: String): LiveData<Resource<Recipe>> {
        return object : NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()) {
            override fun saveCallResult(item: RecipeResponse) {

                // will be null if API key is expired
                if (item.recipe != null) {
                    item.recipe.timestamp = System.currentTimeMillis() / 1000
                    recipeDao.insertRecipe(item.recipe)
                }
            }

            override fun shouldFetch(data: Recipe?): Boolean {
                Log.d(TAG, "shouldFetch: recipe: $data")
                val currentTime = System.currentTimeMillis() / 1000
                Log.d(TAG, "shouldFetch: current time: $currentTime")
                val lastRefresh= data?.timestamp
                Log.d(TAG, "shouldFetch: last refresh: $lastRefresh")
                lastRefresh?.let {
                    val diff = currentTime - it
                    Log.d(TAG, "shouldFetch: it's been " + diff / 60 / 60 / 24 +
                            " days since this recipe was refreshed. 30 days must elapse before refreshing. ")
                }
                data?.timestamp?.let {
                    if (currentTime - it >= Constant.RECIPE_REFRESH_TIME) {
                        Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + true)
                        return true
                    }
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + false)
                return false
            }


            override fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipeById(recipeId)
            }


            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
                return ServiceGenerator.recipeApi.getRecipe(
                    Constant.API_KEY,
                    recipeId
                )
            }
        }.asLiveData()
    }
    companion object {
        private const val TAG = "RecipeRepository"

        @Volatile
        var instance: RecipeRepository? = null

        fun getInstance(context: Context): RecipeRepository {
            return instance?: synchronized(RecipeRepository::class.java) {
                instance?: RecipeRepository(context)
            }

            /** Way #1  */
//            if (instance == null) { // check 1
//                synchronized(this) {
//                    if(instance == null) // check 2
//                    instance = RecipeRepository(context)
//                }
//            }
//            return instance


            /** way #2 */
//             synchronized(this)  {
//                 return instance?: instance?: RecipeRepository(context)
//             }

        }
    }

}
