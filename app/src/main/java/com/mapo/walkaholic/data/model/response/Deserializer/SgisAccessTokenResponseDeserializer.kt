package com.mapo.walkaholic.data.model.response.Deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mapo.walkaholic.data.model.response.SgisAccessTokenResponse
import java.lang.reflect.Type

class SgisAccessTokenResponseDeserializer : JsonDeserializer<SgisAccessTokenResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SgisAccessTokenResponse {
        val deserializedJson = ((json?.asJsonObject
                ?: throw NullPointerException("Response Json String is null"))["result"].asJsonObject
                ?: throw NullPointerException("Response Json String is null"))
        if (deserializedJson["accessTimeout"].asString.toString().trim() == "null" || deserializedJson["accessToken"].asString.toString().trim() == "null") {
            return SgisAccessTokenResponse(true,
                SgisAccessTokenResponse.SGISAccessToken(
                    deserializedJson["accessTimeout"].asString.toString().trim(),
                    deserializedJson["accessToken"].asString.toString().trim()
                )
            )
        } else {
            return SgisAccessTokenResponse(false,
                SgisAccessTokenResponse.SGISAccessToken(
                    deserializedJson["accessTimeout"].asString.toString().trim(),
                    deserializedJson["accessToken"].asString.toString().trim()
                )
            )
        }
    }
}