package io.github.wonshaw.dogceodemo.data.repository

import com.google.common.truth.Truth.assertThat
import io.github.wonshaw.dogceodemo.data.remote.DogCeoApi
import io.github.wonshaw.dogceodemo.data.remote.model.DogBreedsApiResponse
import io.github.wonshaw.dogceodemo.data.remote.model.DogSingleImageApiResponse
import io.github.wonshaw.dogceodemo.domain.model.DogBreed
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DogRepositoryTest {
    private lateinit var repository: DogRepository
    private lateinit var api: DogCeoApi

    @Before
    fun setup() {
        api = mockk()
        repository = DogRepository(api)
    }

    @Test
    fun `getAllBreeds() returns success and caches data`() = runTest {
        val response = DogBreedsApiResponse(
            status = SUCCESS,
            breeds = mapOf(
                "affenpinscher" to emptyList(),
                "australian" to listOf("kelpie", "shepherd"),
                "bakharwal" to listOf("indian")
            )
        )
        coEvery { api.getAllBreeds() } returns response

        val result = repository.getAllBreeds()

        assertThat(result).isInstanceOf(RepositoryResult.Success::class.java)
        val data = (result as RepositoryResult.Success).data
        assertThat(data).containsExactlyElementsIn(
            listOf(
                DogBreed(id = "affenpinscher", breed = "affenpinscher"),
                DogBreed(id = "australian_kelpie", breed = "australian", subBreed = "kelpie"),
                DogBreed(id = "australian_shepherd", breed = "australian", subBreed = "shepherd"),
                DogBreed(id = "bakharwal_indian", breed = "bakharwal", subBreed = "indian"),
            )
        )

        val cachedResult = repository.getAllBreeds()
        assertThat(cachedResult).isInstanceOf(RepositoryResult.Success::class.java)
        val cachedResultData = (cachedResult as RepositoryResult.Success).data
        assertThat(cachedResultData).isEqualTo(data)

        coVerify(exactly = 1) { api.getAllBreeds() }
    }

    @Test
    fun `getAllBreeds() returns error when API call throws exception`() = runTest {
        coEvery { api.getAllBreeds() } throws Exception("Network Error")

        val result = repository.getAllBreeds()

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.exception).isInstanceOf(Exception::class.java)
    }

    @Test
    fun `getAllBreeds() returns error when API call returns error status`() = runTest {
        val errorMessage = "failed"
        val response = DogBreedsApiResponse(
            status = errorMessage,
            breeds = mapOf(
            )
        )
        coEvery { api.getAllBreeds() } returns response

        val result = repository.getAllBreeds()

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.message).isEqualTo(errorMessage)
    }

    @Test
    fun `getRandomBreedImage() returns success`() = runTest {
        val imageUrl = "https://dog.ceo/random.jpg"
        val breed = "retriever"
        val response =
            DogSingleImageApiResponse(status = SUCCESS, imageUrl = imageUrl)
        coEvery { api.getRandomBreedImage(breed) } returns response

        val result = repository.getRandomBreedImage(breed)

        assertThat(result).isInstanceOf(RepositoryResult.Success::class.java)
        val data = (result as RepositoryResult.Success).data
        assertThat(data).isEqualTo(imageUrl)
    }

    @Test
    fun `getRandomBreedImage() returns error when API throws exception`() = runTest {
        val breed = "retriever"
        coEvery { api.getRandomBreedImage(breed) } throws Exception("API Error")

        val result = repository.getRandomBreedImage(breed)

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.exception).isInstanceOf(Exception::class.java)
    }

    @Test
    fun `getRandomBreedImage() returns error when API fails`() = runTest {
        val breed = "retriever"
        val errorMessage = "failed"
        val response = DogSingleImageApiResponse(
            status = errorMessage,
            imageUrl = ""
        )
        coEvery { api.getRandomBreedImage(breed) } returns response

        val result = repository.getRandomBreedImage(breed)

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.message).isEqualTo(errorMessage)
    }

    @Test
    fun `getRandomSubBreedImage() returns success`() = runTest {
        val imageUrl = "https://dog.ceo/random.jpg"
        val breed = "retriever"
        val subBreed = "golden"
        val response =
            DogSingleImageApiResponse(status = SUCCESS, imageUrl = imageUrl)
        coEvery { api.getRandomSubBreedImage(breed, subBreed) } returns response

        val result = repository.getRandomSubBreedImage(breed, subBreed)

        assertThat(result).isInstanceOf(RepositoryResult.Success::class.java)
        val data = (result as RepositoryResult.Success).data
        assertThat(data).isEqualTo(imageUrl)
    }

    @Test
    fun `getRandomSubBreedImage() returns error when API throws exception`() = runTest {
        val breed = "retriever"
        val subBreed = "golden"
        coEvery { api.getRandomSubBreedImage(breed, subBreed) } throws Exception("API Error")

        val result = repository.getRandomSubBreedImage(breed, subBreed)

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.exception).isInstanceOf(Exception::class.java)
    }

    @Test
    fun `getRandomSubBreedImage() returns error when API fails`() = runTest {
        val breed = "retriever"
        val subBreed = "golden"
        val errorMessage = "failed"
        val response = DogSingleImageApiResponse(
            status = errorMessage,
            imageUrl = ""
        )
        coEvery { api.getRandomSubBreedImage(breed, subBreed) } returns response

        val result = repository.getRandomSubBreedImage(breed, subBreed)

        assertThat(result).isInstanceOf(RepositoryResult.Error::class.java)
        val errorResult = result as RepositoryResult.Error
        assertThat(errorResult.message).isEqualTo(errorMessage)
    }
}
