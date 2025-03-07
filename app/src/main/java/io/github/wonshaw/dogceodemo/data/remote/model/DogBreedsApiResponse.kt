package io.github.wonshaw.dogceodemo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class DogBreedsApiResponse(
    @Json(name = "message") val breeds: Map<String, List<String>>,
    @Json(name = "status") val status: String,
)