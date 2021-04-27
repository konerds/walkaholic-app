package com.mapo.walkaholic.data.model.response.Deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.NearMsrstn
import com.mapo.walkaholic.data.model.Weather
import com.mapo.walkaholic.data.model.response.WeatherResponse
import java.lang.reflect.Type

class WeatherResponseDeserializer : JsonDeserializer<WeatherResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WeatherResponse {
        val rawWeatherData = ((((json?.asJsonObject
            ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
            ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
            ?: throw NullPointerException("Response Json String is null"))["items"]?.asJsonObject
            ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("item")?.asJsonArray
        val temperatureCurrent =
        rawWeatherData?.forEachIndexed { i, jsonElement ->
            if (jsonElement.asJsonObject["category"].asString.toString().trim() == "T1H") {
                jsonElement.asJsonObject["fcstValue"].asString.toString().trim()
            }
        }.toString()
        val weatherCode : String =
            rawWeatherData?.forEachIndexed { i, jsonElement ->
                if (jsonElement.asJsonObject["category"].asString.toString().trim() == "PTY") {
                    when(jsonElement.asJsonObject["fcstValue"].asInt) {
                        0 -> {
                            if(jsonElement.asJsonObject["category"].asString.toString().trim() == "SKY") {
                                jsonElement.asJsonObject["fcstValue"].asString.toString().trim()
                            }
                        }
                        1,4,5 -> {

                        }
                        2,6 -> {

                        }
                        3,7 -> {

                        }
                        else -> {

                        }
                    }
                }
            }
        return WeatherResponse(false, Weather(temperatureCurrent, weatherCode))
    }
}