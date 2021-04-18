package com.mapo.walkaholic.data.model

data class Point(
        val id: Int = 0,
        val dist: Double = 0.0,
        val geo: Geo? = null,
        var position: Int = 0
)

data class Geo(
        var lat: Double = 0.0,
        var lng: Double = 0.0
)