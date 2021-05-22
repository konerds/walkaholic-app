package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.response.Deserializer.NearMsrstnResponseDeserializer

@JsonAdapter(NearMsrstnResponseDeserializer::class)
data class NearMsrstnResponse(
    val error: Boolean,
    val nearMsrstn: NearMsrstn
) {
    data class NearMsrstn(
        val sidoName: String,
        val stationName: String
    )
}