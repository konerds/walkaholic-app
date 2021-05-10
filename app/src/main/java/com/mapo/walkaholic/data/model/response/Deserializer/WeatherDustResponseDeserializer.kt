package com.mapo.walkaholic.data.model.response.Deserializer

import android.content.ContentValues
import android.provider.Settings
import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.WeatherDust
import com.mapo.walkaholic.data.model.response.WeatherDustResponse
import com.mapo.walkaholic.ui.global.GlobalApplication
import java.lang.reflect.Type

class WeatherDustResponseDeserializer : JsonDeserializer<WeatherDustResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WeatherDustResponse {
        /* @TODO Extract by current location
           sidoName 서울, 부산., 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종
           stationName 중구, 한강대로, 종로구, 청계천로, 종로, 용산구, 광진구, 성동구, 강변북로, 중랑구 */
        val weatherDustList : ArrayList<WeatherDust> = arrayListOf()
        (((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("items")?.asJsonArray?.forEach { i ->
            Log.i(
                    ContentValues.TAG, "${i.asJsonObject["stationName"].asString} ${i.asJsonObject["o3Grade"].asInt} ${i.asJsonObject["pm10Grade1h"].asInt} ${i.asJsonObject["pm25Grade1h"].asInt}"
            )
                val uvRay = when (i.asJsonObject["o3Grade"].asInt) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우 나쁨"
                    else -> "오류"
                }
                val pmDust = when (i.asJsonObject["pm10Grade1h"].asInt) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우 나쁨"
                    else -> "오류"
                }
                val pmSuperDust = when (i.asJsonObject["pm25Grade1h"].asInt) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우 나쁨"
                    else -> "오류"
                }
                weatherDustList.add(WeatherDust(i.asJsonObject["stationName"].asString.toString().trim(), uvRay, pmDust, pmSuperDust, i.asJsonObject["sidoName"].asString))
        }
        return WeatherDustResponse(false, weatherDustList)
    }
}