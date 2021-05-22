package com.mapo.walkaholic.data.model.response.Deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.response.NearMsrstnResponse
import java.lang.reflect.Type

class NearMsrstnResponseDeserializer : JsonDeserializer<NearMsrstnResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): NearMsrstnResponse {
        (((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["response"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["body"]?.asJsonObject
                ?: throw NullPointerException("Response Json String is null")).getAsJsonArray("items")?.asJsonArray?.forEachIndexed { i, jsonElement ->
            if (i == 0) {
                val sidoName = jsonElement.asJsonObject["addr"].asString.toString().trim().substring(0, 2)
                val stationName = jsonElement.asJsonObject["stationName"].asString.toString().trim()
                return NearMsrstnResponse(false,
                    NearMsrstnResponse.NearMsrstn(sidoName, stationName)
                )
            }
        }
        // Error
        return NearMsrstnResponse(true, NearMsrstnResponse.NearMsrstn("오류", "오류"))
    }
}