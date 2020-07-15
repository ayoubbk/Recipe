package com.bks.recipe.adapters

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bks.recipe.R
import com.bks.recipe.models.Recipe
import com.bumptech.glide.RequestManager
import de.hdodenhof.circleimageview.CircleImageView

class CategoryViewHolder(
    @NonNull itemView: View,
    var listener: OnRecipeListener,
    var requestManager: RequestManager
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryImage: CircleImageView = itemView.findViewById(R.id.category_image)
    var categoryTitle: TextView = itemView.findViewById(R.id.category_title)

    init {
        itemView.setOnClickListener(this)
    }

    fun onBind(recipe: Recipe) {
        val path =
            Uri.parse("android.resource://com.bks.recipe/drawable/" + recipe.imageUrl)
        requestManager
            .load(path)
            .into(categoryImage)
        categoryTitle.text = recipe.title
    }

    override fun onClick(v: View) {
        listener.onCategoryClick(categoryTitle.text.toString())
    }


}