package com.mapo.walkaholic.ui.favoritepath

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemFavoritePathBinding

class FavoritePathDetailAdapter (
    private val arrayListFavoritePath: ArrayList<Theme>,
    private val listener: FavoritePathClickListener
) : RecyclerView.Adapter<FavoritePathDetailAdapter.ItemFavoritePathViewHolder>() {

    private val checkedFavoritePathMap = mutableMapOf<Int, Pair<Boolean, Theme>>()

    init {
        clearSelectedItem()
    }

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
        holder.binding.favoritePathCB.setOnClickListener {
            toggleItemSelected(position)
            listener.onItemClick(checkedFavoritePathMap)
        }
    }

    override fun getItemCount() = arrayListFavoritePath.size

    private fun toggleItemSelected(position: Int) {
        if(checkedFavoritePathMap[position] != null) {
            if (checkedFavoritePathMap[position]!!.first) {
                checkedFavoritePathMap[position] = Pair(false, checkedFavoritePathMap[position]!!.second)
            } else {
                checkedFavoritePathMap[position] = Pair(true, checkedFavoritePathMap[position]!!.second)
            }
        } else { }
    }

    private fun isItemSelected(position: Int): Boolean {
        return if (checkedFavoritePathMap[position] != null) {
            checkedFavoritePathMap[position]!!.first
        } else {
            false
        }
    }

    fun clearSelectedItem() {
        for(i in 0 until arrayListFavoritePath.size) {
            checkedFavoritePathMap[i] = Pair(false, arrayListFavoritePath[i])
        }
    }
}