package com.mapo.walkaholic.ui.favoritepath

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemFavoritePathBinding

class FavoritePathDetailAdapter (
    private val arrayListFavoritePath: ArrayList<Theme>
) : RecyclerView.Adapter<FavoritePathDetailAdapter.ItemFavoritePathViewHolder>() {
    inner class ItemFavoritePathViewHolder(
        val binding: ItemFavoritePathBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setFavoritePathItem(favoritePathItem: Theme) {
            binding.favoritePathItem = favoritePathItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemFavoritePathViewHolder {
        val binding = ItemFavoritePathBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemFavoritePathViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemFavoritePathViewHolder, position: Int) {
        holder.setFavoritePathItem(arrayListFavoritePath[position])
    }

    override fun getItemCount() = arrayListFavoritePath.size
}