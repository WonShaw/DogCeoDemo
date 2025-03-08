package io.github.wonshaw.dogceodemo.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wonshaw.dogceodemo.data.repository.DogRepository
import io.github.wonshaw.dogceodemo.data.repository.RepositoryResult
import io.github.wonshaw.dogceodemo.domain.model.DogBreedQuiz
import javax.inject.Inject

class GetDogBreedQuizUseCase @Inject constructor(
    private val repository: DogRepository,
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context,
    private val converter: ICoilResultToBitmapConverter,
) {
    suspend fun execute(): UseCaseResult<DogBreedQuiz> {
        val allBreedsResult = repository.getAllBreeds()
        if (allBreedsResult is RepositoryResult.Success) {

            val options = allBreedsResult.data.shuffled().take(4)
            val correctOption = options.random()

            val imageResult = if (correctOption.isSubBreed()) {
                repository.getRandomSubBreedImage(correctOption.breed, correctOption.subBreed!!)
            } else {
                repository.getRandomBreedImage(correctOption.breed)
            }

            if (imageResult is RepositoryResult.Success) {
                val imageRequest = ImageRequest.Builder(context).data(imageResult.data).build()
                val bitmapResult = imageLoader.execute(imageRequest)
                if (bitmapResult is SuccessResult) {
                    return UseCaseResult.Success(
                        DogBreedQuiz(
                            imageBitmap = converter.convert(bitmapResult),
                            options = options,
                            correctAnswer = correctOption
                        )
                    )
                } else if (bitmapResult is ErrorResult) {
                    return UseCaseResult.Error(message = "get image error")
                } else {
                    return UseCaseResult.Error(message = "get image error")
                }
            } else {
                return UseCaseResult.Error(message = "get image url error")
            }
        } else {
            return UseCaseResult.Error(message = "get all breeds error")
        }
    }

    fun checkAnswer(quiz: DogBreedQuiz, selectedId: String): Boolean {
        return quiz.isCorrectAnswer(selectedId)
    }
}

interface ICoilResultToBitmapConverter {
    fun convert(result: SuccessResult): ImageBitmap
}

class CoilResultToBitmapConverter : ICoilResultToBitmapConverter {
    override fun convert(result: SuccessResult): ImageBitmap {
        return result.image.toBitmap().asImageBitmap()
    }
}