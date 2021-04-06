package com.mapo.walkaholic.data.model.request

class MapRequestBody(
    private val lng: Double, private val lat: Double,
    private val swlng: Double, private val swlat: Double,
    private val selng: Double, private val selat: Double,
    private val nelng: Double, private val nelat: Double,
    private val nwlng: Double, private val nwlat: Double)