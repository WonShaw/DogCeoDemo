package io.github.wonshaw.dogceodemo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class DogSingleImageApiResponse(
    @Json(name = "message") val imageUrl: String,
    @Json(name = "status") val status: String,
)