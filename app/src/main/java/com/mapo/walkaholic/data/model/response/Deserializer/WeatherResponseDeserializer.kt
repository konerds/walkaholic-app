package com.mapo.walkaholic.data.model.response.Deserializer

import android.content.ContentValues
import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.NearMsrstn
import com.mapo.walkaholic.data.model.Weather
import com.mapo.walkaholic.data.model.response.WeatherResponse
import java.lang.reflect.Type
import java.util.*

class WeatherResponseDeserializer : JsonDeserializer<WeatherResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WeatherResponse {
        val rawWeatherData = ((((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["items"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("item")?.asJsonArray
        Log.i(
                ContentValues.TAG, "$rawWeatherData"
        )
        var temperatureCurrent: String? = null
        rawWeatherData?.forEachIndexed { i, jsonElement ->
            if ((jsonElement.asJsonObject["category"].asString.toString().trim() == "T1H") && (jsonElement.asJsonObject["fcstTime"].asString.toString().trim() == "${((Date().hours + 1).toString())}00")
            ) {
                temperatureCurrent = jsonElement.asJsonObject["fcstValue"].asString.toString().trim()
            } else {
                temperatureCurrent = "오류"
            }
        }
        Log.i(
                ContentValues.TAG, "$temperatureCurrent"
        )
        var weatherCode: String? = null
        rawWeatherData?.forEachIndexed { i, jsonElement ->
            if ((jsonElement.asJsonObject["category"].asString.toString().trim() == "PTY") && (jsonElement.asJsonObject["fcstTime"].asString.toString().trim() == "${((Date().hours + 1).toString())}00")) {
                when (jsonElement.asJsonObject["fcstValue"].asInt) {
                    0 -> {
                        rawWeatherData?.forEachIndexed { i2, jsonElement2 ->
                            if (jsonElement2.asJsonObject["category"].asString.toString().trim() == "SKY" && (jsonElement.asJsonObject["fcstTime"].asString.toString().trim() == "${((Date().hours + 1).toString())}00")) {
                                weatherCode = when(jsonElement2.asJsonObject["fcstValue"].asInt) {
                                    0 -> "맑음"
                                    1 -> "구름"
                                    2 -> "흐림"
                                    else -> "오류"
                                }
                            } else {
                                weatherCode = "오류"
                            }
                        }
                    }
                    1, 4, 5 -> {
                        weatherCode = "비"
                    }
                    2, 6 -> {
                        weatherCode = "진눈개비"
                    }
                    3, 7 -> {
                        weatherCode = "눈"
                    }
                    else -> {
                        weatherCode = "오류"
                    }
                }
            }
        }
        Log.i(
                ContentValues.TAG, "$weatherCode"
        )
        if(temperatureCurrent.toString() == "오류" || weatherCode == "오류") {
            return WeatherResponse(true, Weather(temperatureCurrent.toString(), weatherCode.toString()))
        } else {
            return WeatherResponse(false, Weather(temperatureCurrent.toString(), weatherCode.toString()))
        }
    }
}