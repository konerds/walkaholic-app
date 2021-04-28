package com.mapo.walkaholic.data.model.response.Deserializer

import android.content.ContentValues
import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.TodayWeather
import com.mapo.walkaholic.data.model.response.TodayWeatherResponse
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class TodayWeatherResponseDeserializer : JsonDeserializer<TodayWeatherResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TodayWeatherResponse {
        val currentTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val timeFormat = SimpleDateFormat("HH00", Locale.KOREAN)
        timeFormat.timeZone = currentTimeZone
        val requireDate = Date()
        requireDate.hours += 1
        val rawWeatherData = ((((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["items"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("item")?.asJsonArray
        rawWeatherData?.forEachIndexed { i, jsonElement ->
            if ((jsonElement.asJsonObject["category"].asString.toString().trim() == "T1H") && (jsonElement.asJsonObject["fcstTime"].asString.toString().trim() == timeFormat.format(requireDate))) {
                rawWeatherData?.forEachIndexed { i2, jsonElement2 ->
                    if ((jsonElement2.asJsonObject["category"].asString.toString().trim() == "PTY") && (jsonElement2.asJsonObject["fcstTime"].asString.toString().trim() == "${timeFormat.format(requireDate)}")) {
                        when (jsonElement2.asJsonObject["fcstValue"].asInt) {
                            0 -> {
                                rawWeatherData?.forEachIndexed { i3, jsonElement3 ->
                                    if (jsonElement3.asJsonObject["category"].asString.toString().trim() == "SKY" && (jsonElement3.asJsonObject["fcstTime"].asString.toString().trim() == timeFormat.format(requireDate))) {
                                        return TodayWeatherResponse(false, TodayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim(), when (jsonElement3.asJsonObject["fcstValue"].asInt) {
                                            1 -> "맑음"
                                            2, 3 -> "구름"
                                            4 -> "흐림"
                                            else -> "오류"
                                        }))
                                    } else { }
                                }
                            }
                            1, 4, 5 -> {
                                return TodayWeatherResponse(false, TodayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim(), "비"))
                            }
                            2, 6 -> {
                                return TodayWeatherResponse(false, TodayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim(), "진눈개비"))
                            }
                            3, 7 -> {
                                return TodayWeatherResponse(false, TodayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim(), "눈"))
                            }
                            else -> {
                                return TodayWeatherResponse(true, TodayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim(), "오류"))
                            }
                        }
                    }
                }
            } else { }
        }
        return TodayWeatherResponse(true, TodayWeather("오류", "오류"))
    }
}