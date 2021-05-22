package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.response.Deserializer.WeatherDustResponseDeserializer

@JsonAdapter(WeatherDustResponseDeserializer::class)
data class WeatherDustResponse(
    val error: Boolean,
    val weatherDust: ArrayList<WeatherDust>
) {
    data class WeatherDust(
        val stationName: String,
        val uvRay: String,
        val pmDust: String,
        val pmSuperDust: String,
        val sidoName: String
    )
}