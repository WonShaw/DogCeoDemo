package io.github.wonshaw.dogceodemo.data.remote

import io.github.wonshaw.dogceodemo.data.remote.model.DogBreedsApiResponse
import io.github.wonshaw.dogceodemo.data.remote.model.DogMultipleImagesApiResponse
import io.github.wonshaw.dogceodemo.data.remote.model.DogSingleImageApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DogCeoApi {
    @GET("breeds/list/all")
    suspend fun getAllBreeds(): DogBreedsApiResponse

    @GET("breed/{breed}/images/random")
    suspend fun getRandomBreedImage(
        @Path("breed") breed: String
    ): DogSingleImageApiResponse

    @GET("breed/{breed}/{subBreed}/images/random")
    suspend fun getRandomSubBreedImage(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): DogSingleImageApiResponse
}