package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.WeatherDust
import com.mapo.walkaholic.data.model.response.Deserializer.WeatherDustResponseDeserializer

@JsonAdapter(WeatherDustResponseDeserializer::class)
data class WeatherDustResponse(
        val error: Boolean,
        val weatherDust: ArrayList<WeatherDust>
)