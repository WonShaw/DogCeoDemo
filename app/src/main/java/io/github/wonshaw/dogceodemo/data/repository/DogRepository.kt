package io.github.wonshaw.dogceodemo.data.repository

import io.github.wonshaw.dogceodemo.data.remote.DogCeoApi
import io.github.wonshaw.dogceodemo.domain.model.DogBreed
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DogRepository @Inject constructor(private val api: DogCeoApi) {
    private var allBreedsCache : List<DogBreed>? = null

    suspend fun getAllBreeds(): RepositoryResult<List<DogBreed>> {
        val localAllBreedsCache = allBreedsCache
        if (localAllBreedsCache != null) {
            return RepositoryResult.Success(localAllBreedsCache)
        }
        return try {
            val response = api.getAllBreeds()
            if (response.status == SUCCESS) {
                val allBreeds = mutableListOf<DogBreed>()
                response.breeds.forEach { (breed, subBreeds) ->
                    if (subBreeds.isNotEmpty()) {
                        subBreeds.forEach { subBreed ->
                            allBreeds.add(DogBreed(id = "${breed}_${subBreed}", breed = breed, subBreed = subBreed))
                        }
                    } else {
                        allBreeds.add(DogBreed(id = breed, breed = breed))
                    }
                }
                allBreedsCache = allBreeds
                RepositoryResult.Success(allBreeds)
            } else {
                RepositoryResult.Error(message = response.status)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(exception = e)
        }
    }

    suspend fun getRandomBreedImage(breed: String): RepositoryResult<String> {
        return try {
            val response = api.getRandomBreedImage(breed)
            if (response.status == SUCCESS) {
                RepositoryResult.Success(response.imageUrl)
            } else {
                RepositoryResult.Error(message = response.status)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(exception = e)
        }
    }

    suspend fun getRandomSubBreedImage(breed: String, subBreed: String): RepositoryResult<String> {
        return try {
            val response = api.getRandomSubBreedImage(breed, subBreed)
            if (response.status == SUCCESS) {
                RepositoryResult.Success(response.imageUrl)
            } else {
                RepositoryResult.Error(message = response.status)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(exception = e)
        }
    }
}

// VisibleForTesting
const val SUCCESS = "success"