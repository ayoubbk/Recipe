package com.bks.recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import com.bks.recipe.models.Recipe
import com.bks.recipe.util.Resource
import com.bks.recipe.util.Status
import com.bks.recipe.util.getViewModelFactory
import com.bks.recipe.viewmodels.RecipeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class RecipeActivity : BaseActivity() {
    // UI components
    private var mRecipeImage: AppCompatImageView? = null
    private var mRecipeTitle: TextView? = null
    private var mRecipeRank: TextView? = null
    private var mRecipeIngredientsContainer: LinearLayout? = null
    private var mScrollView: ScrollView? = null

    // Lazy instantiate RecipeListViewModel
    private val mRecipeViewModel by viewModels<RecipeViewModel> { getViewModelFactory()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        mRecipeImage = findViewById(R.id.recipe_image)
        mRecipeTitle = findViewById(R.id.recipe_title)
        mRecipeRank = findViewById(R.id.recipe_social_score)
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container)
        mScrollView = findViewById(R.id.parent)

        getIncomingIntent()
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("recipe")) {
            val recipe: Recipe = intent.getParcelableExtra("recipe")
            Log.d(RecipeActivity.TAG, "getIncomingIntent: " + recipe.title)
            subscribeObservers(recipe.recipe_id)
        }
    }

    private fun subscribeObservers(recipeId: String) {
        mRecipeViewModel.searchRecipeApi(recipeId)
            .observe(this, Observer<Resource<Recipe>> { recipeResource ->
                if (recipeResource != null) {
                    if (recipeResource.data != null) {
                        when (recipeResource.status) {
                            Status.LOADING -> {
                                showProgressBar(true)
                            }
                            Status.ERROR -> {
                                Log.e(TAG, "onChanged: status: ERROR, Recipe: " + recipeResource.data.title)
                                Log.e(TAG, "onChanged: ERROR message: " + recipeResource.message)
                                showParent()
                                showProgressBar(false)
                                setRecipeProperties(recipeResource.data)
                            }
                            Status.SUCCESS -> {
                                Log.d(TAG, "onChanged: cache has been refreshed.")
                                Log.d(TAG, "onChanged: status: SUCCESS, Recipe: " + recipeResource.data.title)
                                showParent()
                                showProgressBar(false)
                                setRecipeProperties(recipeResource.data)
                            }
                        }
                    }
                }
            })
    }

    private fun setRecipeProperties(recipe: Recipe?) {
        if (recipe != null) {
            val options = RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background)
            Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(recipe.imageUrl)
                .into(mRecipeImage!!)
            mRecipeTitle?.setText(recipe.title)
            mRecipeRank!!.text = java.lang.String.valueOf(Math.round(recipe.socialRank))
            setIngredients(recipe)
        }
    }

    private fun setIngredients(recipe: Recipe?) {
        mRecipeIngredientsContainer!!.removeAllViews()
        if (!recipe?.ingredients.isNullOrEmpty()) {
            recipe?.ingredients?.forEach {
                val textView = TextView(this)
                textView.text = it
                textView.textSize = 15f
                textView.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                mRecipeIngredientsContainer!!.addView(textView)
            }
        } else {
            val textView = TextView(this)
            textView.text = "Error retrieving ingredients.\nCheck network connection."
            textView.textSize = 15f
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            mRecipeIngredientsContainer!!.addView(textView)
        }
    }

    private fun showParent() {
        mScrollView!!.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "RecipeActivity"
    }
}
