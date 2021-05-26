package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.MarkerInfo

data class MarkerLatLngResponse(
    val code: String,
    val message: String,
    val data: ArrayList<MarkerInfo>
)