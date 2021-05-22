package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.response.Deserializer.TodayWeatherResponseDeserializer

@JsonAdapter(TodayWeatherResponseDeserializer::class)
data class TodayWeatherResponse(
    val error: Boolean,
    val todayWeather: TodayWeather
) {
    data class TodayWeather(
        val temperatureCurrent : String,
        val weatherCode : String,
        val weatherText : String
    )
}