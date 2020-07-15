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
import com.bks.recipe.requests.response.RecipeSearchResponse
import com.bks.recipe.util.Constant
import com.bks.recipe.util.NetworkBoundResource
import com.bks.recipe.util.Resource

class RecipeRepository(private val context: Context) {
    private val recipeDao: RecipeDao = RecipeDatabase.getInstance(context).getRecipeDao() // val mean readOnly

    fun searchRecipesApi(query: String, pageNumber: Int): LiveData<Resource<List<Recipe>>> {
        return object :
            NetworkBoundResource<List<Recipe>, RecipeSearchResponse?>(AppExecutors.getInstance()!!) {

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

//    fun searchRecipeApi(recipeId: String?): LiveData<Resource<Recipe>> {
//        return object : NetworkBoundResource<Recipe, RecipeResponse>() {
//            override fun saveCallResult(item: RecipeResponse) {
//
//            }
//
//            override fun shouldFetch(data: Recipe?): Boolean {
//                return false
//            }
//
//            override fun loadFromDb(): LiveData<Recipe> {
//
//            }
//
//            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
//
//            }
//        }.asLiveData()
//    }

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
