package io.github.wonshaw.dogceodemo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class DogMultipleImagesApiResponse(
    @Json(name = "message") val images: List<String> = emptyList(),
    @Json(name = "status") val status: String = "",
)