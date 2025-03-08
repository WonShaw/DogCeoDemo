package io.github.wonshaw.dogceodemo.data.remote

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class DogCeoApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: DogCeoApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(DogCeoApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllBreeds returns correct response`() = runTest {
        val responseBody = """
            {
                "message": {
                    "affenpinscher": [],
                    "australian": [
                        "kelpie",
                        "shepherd"
                    ],
                    "springer": [
                        "english"
                    ]
                },
                "status": "success"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val response = api.getAllBreeds()

        val request = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
        assertThat(request!!.path).isEqualTo("/breeds/list/all")


        assertThat(response.status).isEqualTo("success")
        assertThat(response.breeds).isEqualTo(
            mapOf(
                "affenpinscher" to emptyList(),
                "australian" to listOf("kelpie", "shepherd"),
                "springer" to listOf("english"),
            )
        )
    }

    @Test
    fun `getRandomBreedImage returns correct image URL`() = runTest {
        val responseBody = """
            {
                "message": "https://images.dog.ceo/breeds/spaniel-blenheim/n02086646_2731.jpg",
                "status": "success"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val response = api.getRandomBreedImage("husky")
        val request = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
        assertThat(request!!.path).isEqualTo("/breed/husky/images/random")

        assertThat(response.status).isEqualTo("success")
        assertThat(response.imageUrl).startsWith("https://images.dog.ceo/breeds/spaniel-blenheim/n02086646_2731.jpg")
    }

    @Test
    fun `getRandomSubBreedImage returns correct image URL`() = runTest {
        val responseBody = """
            {
                "message": "https://images.dog.ceo/breeds/spaniel-blenheim/n02086646_2731.jpg",
                "status": "success"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        val response = api.getRandomSubBreedImage("main", "sub")

        val request = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
        assertThat(request!!.path).isEqualTo("/breed/main/sub/images/random")

        assertThat(response.status).isEqualTo("success")
        assertThat(response.imageUrl).startsWith("https://images.dog.ceo/breeds/spaniel-blenheim/n02086646_2731.jpg")
    }
}