package com.mapo.walkaholic.data.model.response.Deserializer

import android.content.ContentValues
import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.YesterdayWeather
import com.mapo.walkaholic.data.model.response.YesterdayWeatherResponse
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class YesterdayWeatherResponseDeserializer : JsonDeserializer<YesterdayWeatherResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): YesterdayWeatherResponse {
        val currentTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val timeFormat = SimpleDateFormat("HH00", Locale.KOREAN)
        timeFormat.timeZone = currentTimeZone
        val requireDate = Date()
        requireDate.date -= 1
        requireDate.hours += 2
        Log.i(
                ContentValues.TAG, "Yesterday Require Date : ${requireDate}"
        )
        val rawWeatherData = ((((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["items"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("item")?.asJsonArray
        Log.i(
                ContentValues.TAG, "Yesterday Raw Data : ${rawWeatherData}"
        )
        rawWeatherData?.forEachIndexed { i, jsonElement ->
            if ((jsonElement.asJsonObject["category"].asString.toString().trim() == "T1H") && (jsonElement.asJsonObject["fcstTime"].asString.toString().trim() == timeFormat.format(requireDate))) {
                Log.i(
                        ContentValues.TAG, "Yesterday category & fcstTime : ${jsonElement.asJsonObject["category"].asString.toString().trim()} ${jsonElement.asJsonObject["fcstTime"].asString.toString().trim()}"
                )
                return YesterdayWeatherResponse(false, YesterdayWeather(jsonElement.asJsonObject["fcstValue"].asString.toString().trim()))
            } else { }
        }
        return YesterdayWeatherResponse(true, YesterdayWeather("오류"))
    }
}