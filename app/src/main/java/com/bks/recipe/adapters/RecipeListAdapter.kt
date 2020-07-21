package com.bks.recipe.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bks.recipe.R
import com.bks.recipe.models.Recipe
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_recipe_list_item.view.*

/**
 * Adapter class fo RecipeRecyclerView. Best practice way to update recycler view items asynchronously.
 * You pretty much guarantee that if you rotate your screen, your list item is set  again.
 * With this way, make sure that your UI does not freeze if your UI freeze
 *
 */

class RecipeListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //It can be used to calculate updates for a RecyclerView Adapter. See {@link ListAdapter} and
    // * {@link AsyncListDiffer} which can simplify the use of DiffUtil on a background thread.
     val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Recipe>() {

        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.recipe_id == newItem.recipe_id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

    }

    // Makes updates when submitting new list asynchronously
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return RecipeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_recipe_list_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecipeViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Recipe>) {
        differ.submitList(list)
    }

    class RecipeViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Recipe) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            itemView.recipe_title.text = item.title
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .into(itemView.recipe_image)

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Recipe)
    }
}
