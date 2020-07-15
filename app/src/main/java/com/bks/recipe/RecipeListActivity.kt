package com.bks.recipe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bks.recipe.adapters.OnRecipeListener
import com.bks.recipe.adapters.RecipeListAdapter
import com.bks.recipe.adapters.RecipeRecyclerAdapter
import com.bks.recipe.models.Recipe
import com.bks.recipe.repositories.RecipeRepository
import com.bks.recipe.util.Constant.Companion.QUERY_EXHAUSTED
import com.bks.recipe.util.Resource
import com.bks.recipe.util.Status
import com.bks.recipe.util.TopSpacingItemDecoration
import com.bks.recipe.util.ViewModelFactory
import com.bks.recipe.viewmodels.RecipeListViewModel
import com.bks.recipe.viewmodels.ViewState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider


class RecipeListActivity : BaseActivity(), OnRecipeListener, RecipeListAdapter.Interaction {

    private lateinit var recyclerView : RecyclerView
    private lateinit var searchView : SearchView
    private lateinit var recipeRepository : RecipeRepository
    private var recipeRecyclerAdapter : RecipeRecyclerAdapter? = null

    // Lazy instantiate RecipeListViewModel
    private val viewModel by viewModels<RecipeListViewModel> { ViewModelFactory(recipeRepository, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        recyclerView = findViewById(R.id.recipe_list)
        searchView = findViewById(R.id.search_view)

        initRepository()

        initRecyclerView()
        initSearchView()
        subscribeObservers()
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
    }

    private fun initRepository() {
        recipeRepository = RecipeRepository.getInstance(this)
    }

    private fun initRecyclerView() {

        val viewPreloader: ViewPreloadSizeProvider<String> = ViewPreloadSizeProvider()
        recipeRecyclerAdapter = RecipeRecyclerAdapter(this, initGlide(), viewPreloader)
        val itemDecorator = TopSpacingItemDecoration(30)
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val preloader = RecyclerViewPreloader(
            Glide.with(this),
            recipeRecyclerAdapter!!,
            viewPreloader,
            30
        )
        recyclerView.addOnScrollListener(preloader)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!recyclerView.canScrollVertically(1) && viewModel.viewState.value == ViewState.RECIPES) {
                    viewModel.searchNextPage()
                }
            }
        })

        recyclerView.adapter = recipeRecyclerAdapter
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchRecipeApi(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun initGlide() : RequestManager {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)

        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun subscribeObservers() {
        viewModel.recipes.observe(this, Observer<Resource<List<Recipe>>> { listResource ->
            if(listResource != null) {
                Log.d(TAG, "onChanged: status: " + listResource.status)
                if (listResource.data != null) {
                    when (listResource.status) {
                        Status.LOADING -> {
                            if (viewModel.pageNumber > 1) {
                                recipeRecyclerAdapter?.displayLoading()
                            } else {
                                recipeRecyclerAdapter?.displayOnlyLoading()
                            }
                        }
                        Status.ERROR -> {
                            Log.e(TAG, "onChanged: cannot refresh the cache.")
                            Log.e(TAG, "onChanged: ERROR message: " + listResource.message)
                            Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size)
                            recipeRecyclerAdapter?.hideLoading()
                            recipeRecyclerAdapter?.setRecipes(ArrayList(listResource.data))
                            Toast.makeText(this@RecipeListActivity, listResource.message, Toast.LENGTH_SHORT).show()
                            if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                recipeRecyclerAdapter?.setQueryExhausted()
                            }
                        }
                        Status.SUCCESS -> {
                            Log.d(TAG, "onChanged: cache has been refreshed.")
                            Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size)
                            recipeRecyclerAdapter?.hideLoading()
                            recipeRecyclerAdapter?.setRecipes(ArrayList(listResource.data))
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this,
            Observer<ViewState> { viewState ->
                if (viewState != null) {
                    when (viewState) {
                        ViewState.RECIPES -> {
                            // recipes will show automatically from other observer
                        }
                        ViewState.CATEGORIES -> {
                            displaySearchCategories()
                        }
                    }
                }
            })
    }

    private fun searchRecipeApi(query : String) {
        recyclerView.smoothScrollToPosition(0)
        viewModel.searchRecipesApi(query, 1)
        searchView.clearFocus()
    }

    override fun onRecipeClick(position: Int) {
        Log.d(TAG, "onRecipeClick: CLICKED")
    }

    override fun onCategoryClick(category: String) {
        searchRecipeApi(category)
    }

    override fun onItemSelected(position: Int, item: Recipe) {
        println("DEBUG CLICKED $position")
        println("DEBUG CLICKED $item")
    }

    private fun displaySearchCategories() {
        recipeRecyclerAdapter?.displaySearchCategories()
    }

    companion object {
        private const val TAG = "RecipeListActivity"
    }

    override fun onBackPressed() {
        if(viewModel.viewState.value == ViewState.CATEGORIES) {
            super.onBackPressed()
        }
        else {
            viewModel.cancelSearchRequest()
            viewModel.setViewCategories()
        }
    }
}