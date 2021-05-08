package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.SGISAccessToken
import com.mapo.walkaholic.data.model.response.Deserializer.SgisAccessTokenResponseDeserializer

@JsonAdapter(SgisAccessTokenResponseDeserializer::class)
data class SgisAccessTokenResponse(
        val error: Boolean,
        val sgisAccessToken: SGISAccessToken
)