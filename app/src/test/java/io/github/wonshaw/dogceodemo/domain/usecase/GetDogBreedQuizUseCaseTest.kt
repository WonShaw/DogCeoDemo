package io.github.wonshaw.dogceodemo.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import coil3.Image
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.google.common.truth.Truth.assertThat
import io.github.wonshaw.dogceodemo.data.repository.DogRepository
import io.github.wonshaw.dogceodemo.data.repository.RepositoryResult
import io.github.wonshaw.dogceodemo.domain.model.DogBreed
import io.github.wonshaw.dogceodemo.domain.model.DogBreedQuiz
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetDogBreedQuizUseCaseTest {
    private lateinit var useCase: GetDogBreedQuizUseCase
    private lateinit var repository: DogRepository
    private lateinit var context: Context
    private lateinit var imageLoader: ImageLoader
    private lateinit var converter: ICoilResultToBitmapConverter

    @Before
    fun setup() {
        repository = mockk()
        context = mockk()
        imageLoader = mockk()
        converter = mockk()
        useCase = GetDogBreedQuizUseCase(repository, imageLoader, context, converter)
    }

    @Test
    fun `execute() returns quiz successfully`() = runTest {
        val dogBreeds = listOf(
            DogBreed(id = "affenpinscher", breed = "affenpinscher"),
            DogBreed(id = "african", breed = "african"),
            DogBreed(id = "australian_kelpie", breed = "australian", subBreed = "kelpie"),
            DogBreed(id = "australian_shepherd", breed = "australian", subBreed = "shepherd"),
            DogBreed(id = "bakharwal_indian", breed = "bakharwal", subBreed = "indian"),
        )

        coEvery { repository.getAllBreeds() } returns RepositoryResult.Success(dogBreeds)
        coEvery {
            repository.getRandomSubBreedImage(
                any(),
                any()
            )
        } returns RepositoryResult.Success("https://host/sub/image")
        coEvery { repository.getRandomBreedImage(any()) } returns RepositoryResult.Success("https://host/image")

        val mockBitmap: Image = mockk()
        val mockRequest: ImageRequest = mockk()
        coEvery { imageLoader.execute(any()) } returns SuccessResult(mockBitmap, mockRequest)

        every { converter.convert(any()) } returns mockk<Bitmap>()

        val result = useCase.execute()

        assertThat(result).isInstanceOf(UseCaseResult.Success::class.java)
        val data = (result as UseCaseResult.Success).data
        assertThat(data.options).hasSize(4)
        assertThat(data.options).containsNoDuplicates()
        assertThat(data.correctAnswer).isIn(data.options)

        coVerify(exactly = 1) { repository.getAllBreeds() }
        if (data.correctAnswer.isSubBreed()) {
            coVerify(exactly = 1) {
                repository.getRandomSubBreedImage(
                    data.correctAnswer.breed,
                    data.correctAnswer.subBreed!!
                )
            }
        } else {
            coVerify(exactly = 1) { repository.getRandomBreedImage(data.correctAnswer.breed) }
        }
        coVerify(exactly = 1) { imageLoader.execute(any()) }
    }

    @Test
    fun `execute() returns error when getAllBreeds fails`() = runTest {
        coEvery { repository.getAllBreeds() } returns RepositoryResult.Error(message = "get all breeds error")

        val result = useCase.execute()

        assertThat(result).isInstanceOf(UseCaseResult.Error::class.java)

        coVerify(exactly = 1) { repository.getAllBreeds() }
        coVerify(exactly = 0) { repository.getRandomBreedImage(any()) }
        coVerify(exactly = 0) { repository.getRandomSubBreedImage(any(), any()) }
        coVerify(exactly = 0) { imageLoader.execute(any()) }
    }

    @Test
    fun `execute() returns error when subBreed image url retrieval fails`() = runTest {
        coEvery { repository.getAllBreeds() } returns RepositoryResult.Success(
            listOf(DogBreed("retriever_golden", "retriever", "golden"))
        )
        coEvery { repository.getRandomSubBreedImage(any(), any()) } returns RepositoryResult.Error(
            message = "get image url error"
        )

        val result = useCase.execute()

        assertThat(result).isInstanceOf(UseCaseResult.Error::class.java)

        coVerify(exactly = 1) { repository.getAllBreeds() }
        coVerify(exactly = 1) { repository.getRandomSubBreedImage(any(), any()) }
        coVerify(exactly = 0) { imageLoader.execute(any()) }
    }

    @Test
    fun `execute() returns error when breed image url retrieval fails`() = runTest {
        coEvery { repository.getAllBreeds() } returns RepositoryResult.Success(
            listOf(DogBreed("retriever", "retriever"))
        )
        coEvery { repository.getRandomBreedImage(any()) } returns RepositoryResult.Error(message = "get image url error")

        val result = useCase.execute()

        assertThat(result).isInstanceOf(UseCaseResult.Error::class.java)

        coVerify(exactly = 1) { repository.getAllBreeds() }
        coVerify(exactly = 1) { repository.getRandomBreedImage(any()) }
        coVerify(exactly = 0) { imageLoader.execute(any()) }
    }

    @Test
    fun `execute() returns error when imageLoader fails`() = runTest {
        coEvery { repository.getAllBreeds() } returns RepositoryResult.Success(
            listOf(DogBreed("retriever", "retriever"))
        )
        coEvery { repository.getRandomBreedImage(any()) } returns RepositoryResult.Success("https://host/image")

        val errorResult: ErrorResult = mockk()
        coEvery { imageLoader.execute(any()) } returns errorResult

        val result = useCase.execute()

        assertThat(result).isInstanceOf(UseCaseResult.Error::class.java)

        coVerify(exactly = 1) { repository.getAllBreeds() }
        coVerify(exactly = 1) { repository.getRandomBreedImage(any()) }
        coVerify(exactly = 1) { imageLoader.execute(any()) }
    }

    @Test
    fun `checkAnswer() returns true when answer is correct`() {
        val correctAnswer = DogBreed(id = "australian_kelpie", breed = "australian", subBreed = "kelpie")
        val quiz = DogBreedQuiz(
            mockk(),
            listOf(
                DogBreed(id = "affenpinscher", breed = "affenpinscher"),
                DogBreed(id = "african", breed = "african"),
                correctAnswer,
                DogBreed(id = "australian_shepherd", breed = "australian", subBreed = "shepherd"),
            ),
            correctAnswer,
        )

        assertThat(useCase.checkAnswer(quiz, correctAnswer.id)).isTrue()
    }

    @Test
    fun `checkAnswer() returns false when answer is incorrect`() {
        val correctAnswer = DogBreed(id = "australian_kelpie", breed = "australian", subBreed = "kelpie")
        val quiz = DogBreedQuiz(
            mockk(),
            listOf(
                DogBreed(id = "affenpinscher", breed = "affenpinscher"),
                DogBreed(id = "african", breed = "african"),
                correctAnswer,
                DogBreed(id = "australian_shepherd", breed = "australian", subBreed = "shepherd"),
            ),
            correctAnswer,
        )

        assertThat(useCase.checkAnswer(quiz, "australian_shepherd")).isFalse()
        assertThat(useCase.checkAnswer(quiz, "african")).isFalse()
        assertThat(useCase.checkAnswer(quiz, "african")).isFalse()
    }
}