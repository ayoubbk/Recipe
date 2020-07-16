package com.bks.recipe.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bks.recipe.R
import com.bks.recipe.models.Recipe
import com.bks.recipe.util.Constant
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider

class RecipeRecyclerAdapter(
    private val mOnRecipeListener: OnRecipeListener,
    private val requestManager: RequestManager,
    private val preloadSizeProvider: ViewPreloadSizeProvider<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>(), PreloadModelProvider<String> {
    private var mRecipes: ArrayList<Recipe>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        return when (viewType) {

            CATEGORY_TYPE -> {
                view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_category_list_item, viewGroup, false)
                CategoryViewHolder(view, mOnRecipeListener, requestManager)
            }

            RECIPE_TYPE -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                RecipeViewHolder(
                    view,
                    mOnRecipeListener,
                    requestManager,
                    preloadSizeProvider
                )
            }

            EXHAUSTED_TYPE -> {
                view  = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_search_exhausted, viewGroup, false)
                SearchExhaustedViewHolder(view)
            }

            LOADING_TYPE -> {
                view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_loading_list_item, viewGroup, false)
                LoadingViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider);
            }
        }
    }

    override fun onBindViewHolder(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {
        val itemViewType = getItemViewType(i)
        if (itemViewType == RECIPE_TYPE) {
            (viewHolder as RecipeViewHolder).onBind(mRecipes!![i])
        } else if (itemViewType == CATEGORY_TYPE) {
            (viewHolder as CategoryViewHolder).onBind(mRecipes!![i])
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            mRecipes!![position].socialRank == -1f -> {
                CATEGORY_TYPE
            }
            mRecipes!![position].title == "LOADING..." -> {
                LOADING_TYPE
            }
            mRecipes!![position].title == "EXHAUSTED..." -> {
                EXHAUSTED_TYPE
            }
            else -> {
                RECIPE_TYPE
            }
        }
    }



    private fun clearRecipesList() {
        if (mRecipes == null) {
            mRecipes = ArrayList<Recipe>()
        } else {
            mRecipes!!.clear()
        }
        notifyDataSetChanged()
    }

    fun setQueryExhausted() {
        hideLoading()
        val exhaustedRecipe = Recipe(title = "EXHAUSTED...")
        mRecipes!!.add(exhaustedRecipe)
        notifyDataSetChanged()
    }

    fun hideLoading() {
        if (isLoading) {
            if (mRecipes!![0].title == "LOADING...") {
                mRecipes!!.removeAt(0)
            } else if (mRecipes!![mRecipes!!.size - 1].equals("LOADING...")) {
                mRecipes!!.removeAt(mRecipes!!.size - 1)
            }
            notifyDataSetChanged()
        }
    }

    // display loading during search request
    fun displayOnlyLoading() {
        clearRecipesList()
        val recipe = Recipe(title = "LOADING...")
        mRecipes?.add(recipe)
        notifyDataSetChanged()
    }

    // pagination loading
    fun displayLoading() {
        if (mRecipes == null) {
            mRecipes = ArrayList<Recipe>()
        }
        if (!isLoading) {
            val recipe = Recipe(title = "LOADING...")
            mRecipes!!.add(recipe)
            notifyDataSetChanged()
        }
    }

    private val isLoading: Boolean
        private get() {
            if (mRecipes != null) {
                if (mRecipes!!.size > 0) {
                    if (mRecipes!![mRecipes!!.size - 1].title == "LOADING...") {
                        return true
                    }
                }
            }
            return false
        }

    fun displaySearchCategories() {
        val categories: ArrayList<Recipe> = ArrayList<Recipe>()
        for (i in 0 until Constant.DEFAULT_SEARCH_CATEGORIES.size) {
            val recipe = Recipe(title = Constant.DEFAULT_SEARCH_CATEGORIES[i],
                                imageUrl = Constant.DEFAULT_SEARCH_CATEGORY_IMAGES[i],
                                socialRank = -1f)
            categories.add(recipe)
        }
        mRecipes = categories
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return if (mRecipes != null) {
            mRecipes!!.size
        } else 0
    }

    fun setRecipes(recipes: ArrayList<Recipe>?) {
        mRecipes = recipes
        notifyDataSetChanged()
    }

    fun getSelectedRecipe(position: Int): Recipe? {
        if (mRecipes != null) {
            if (mRecipes!!.size > 0) {
                return mRecipes!![position]
            }
        }
        return null
    }

    @NonNull
    override fun getPreloadItems(position: Int): List<String> {
        val url: String = mRecipes!![position].imageUrl
        return if (TextUtils.isEmpty(url)) {
            emptyList()
        } else listOf(url)
    }

    @Nullable
    override fun getPreloadRequestBuilder(@NonNull item: String): RequestBuilder<*> {
        return requestManager.load(item)
    }

    companion object {
        private const val RECIPE_TYPE = 1
        private const val LOADING_TYPE = 2
        private const val CATEGORY_TYPE = 3
        private const val EXHAUSTED_TYPE = 4
    }

}