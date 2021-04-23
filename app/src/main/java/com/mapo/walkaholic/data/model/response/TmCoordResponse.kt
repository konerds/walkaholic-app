package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.TmCoord
import com.mapo.walkaholic.data.model.response.Deserializer.TMCoordResponseDeserializer

@JsonAdapter(TMCoordResponseDeserializer::class)
data class TmCoordResponse(
        val error: Boolean,
        val tmCoord: TmCoord
)