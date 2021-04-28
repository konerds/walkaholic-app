package com.mapo.walkaholic.data.model.response.Deserializer

import android.content.ContentValues
import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.SGISAccessToken
import com.mapo.walkaholic.data.model.WeatherDust
import com.mapo.walkaholic.data.model.response.SGISAccessTokenResponse
import com.mapo.walkaholic.data.model.response.WeatherDustResponse
import java.lang.reflect.Type

class SGISAccessTokenResponseDeserializer : JsonDeserializer<SGISAccessTokenResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SGISAccessTokenResponse {
        val deserializedJson = ((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["result"].asJsonObject
                ?: throw NullPointerException("Response Json String is null"))
        if (deserializedJson["accessTimeout"].asString.toString().trim() == "null" || deserializedJson["accessToken"].asString.toString().trim() == "null") {
            return SGISAccessTokenResponse(true, SGISAccessToken(deserializedJson["accessTimeout"].asString.toString().trim(), deserializedJson["accessToken"].asString.toString().trim()))
        } else {
            return SGISAccessTokenResponse(false, SGISAccessToken(deserializedJson["accessTimeout"].asString.toString().trim(), deserializedJson["accessToken"].asString.toString().trim()))
        }
    }
}