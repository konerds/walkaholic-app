package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.Weather
import com.mapo.walkaholic.data.model.response.Deserializer.WeatherResponseDeserializer

@JsonAdapter(WeatherResponseDeserializer::class)
data class WeatherResponse(
    val error: Boolean,
    val weather: Weather
)