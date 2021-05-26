package com.mapo.walkaholic.ui.main.map

interface MapFacilitiesClickListener {
    fun onItemClick(position: Int, facilitiesId: Int, isSelected: Boolean)
}