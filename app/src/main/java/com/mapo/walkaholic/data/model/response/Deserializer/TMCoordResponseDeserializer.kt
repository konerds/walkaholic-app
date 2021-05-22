package com.mapo.walkaholic.data.model.response.Deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.response.TmCoordResponse
import java.lang.reflect.Type

class TMCoordResponseDeserializer : JsonDeserializer<TmCoordResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TmCoordResponse {
        val deserializedJson = ((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["result"].asJsonObject
                ?: throw NullPointerException("Response Json String is null"))
        if (deserializedJson["posX"].asString.toString().trim() == "null" || deserializedJson["posY"].asString.toString().trim() == "null") {
            return TmCoordResponse(true,
                TmCoordResponse.TmCoord(
                    deserializedJson["posX"].asString.toString().trim(),
                    deserializedJson["posY"].asString.toString().trim()
                )
            )
        } else {
            return TmCoordResponse(false,
                TmCoordResponse.TmCoord(
                    deserializedJson["posX"].asString.toString().trim(),
                    deserializedJson["posY"].asString.toString().trim()
                )
            )
        }
    }
}