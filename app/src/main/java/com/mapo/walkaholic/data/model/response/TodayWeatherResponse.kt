package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.TodayWeather
import com.mapo.walkaholic.data.model.response.Deserializer.TodayWeatherResponseDeserializer

@JsonAdapter(TodayWeatherResponseDeserializer::class)
data class TodayWeatherResponse(
    val error: Boolean,
    val todayWeather: TodayWeather
)