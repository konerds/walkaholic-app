package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.YesterdayWeather
import com.mapo.walkaholic.data.model.response.Deserializer.YesterdayWeatherResponseDeserializer

@JsonAdapter(YesterdayWeatherResponseDeserializer::class)
data class YesterdayWeatherResponse(
    val error: Boolean,
    val yesterdayWeather: YesterdayWeather
)