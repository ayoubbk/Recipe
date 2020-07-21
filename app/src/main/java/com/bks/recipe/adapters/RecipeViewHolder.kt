package com.bks.recipe.adapters

import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bks.recipe.R
import com.bks.recipe.models.Recipe
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlin.math.roundToInt

class RecipeViewHolder(
    @NonNull itemView: View,
    var onRecipeListener: OnRecipeListener,
    var requestManager: RequestManager,
    var viewPreloadSizeProvider: ViewPreloadSizeProvider<*>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var title: TextView = itemView.findViewById(R.id.recipe_title)
    private var publisher: TextView = itemView.findViewById(R.id.recipe_publisher)
    private var socialScore: TextView = itemView.findViewById(R.id.recipe_social_score)
    private var image: AppCompatImageView = itemView.findViewById(R.id.recipe_image)

    init {
        itemView.setOnClickListener(this)
    }

    fun onBind(recipe: Recipe) {
        requestManager
            .load(recipe.imageUrl)
            .into(image)
        title.text = recipe.title
        publisher.text = recipe.publisher
        socialScore.text = recipe.socialRank.roundToInt().toString()
        viewPreloadSizeProvider.setView(image)
    }

    override fun onClick(v: View) {
        onRecipeListener.onRecipeClick(adapterPosition)
    }

}
