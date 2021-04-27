package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Weather

data class WeatherResponse(
    val error: Boolean,
    val weather: Weather
)