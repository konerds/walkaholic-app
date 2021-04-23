package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.JsonAdapter
import com.mapo.walkaholic.data.model.SGISAccessToken
import com.mapo.walkaholic.data.model.response.Deserializer.SGISAccessTokenResponseDeserializer

@JsonAdapter(SGISAccessTokenResponseDeserializer::class)
data class SGISAccessTokenResponse(
        val error: Boolean,
        val sgisAccessToken: SGISAccessToken
)