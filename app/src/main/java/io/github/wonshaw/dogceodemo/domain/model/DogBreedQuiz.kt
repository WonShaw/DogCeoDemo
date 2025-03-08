package io.github.wonshaw.dogceodemo.domain.model

import androidx.compose.ui.graphics.ImageBitmap

class DogBreedQuiz(
    val imageBitmap: ImageBitmap,
    val options: List<DogBreed>,
    val correctAnswer: DogBreed,
) {
    fun isCorrectAnswer(id: String): Boolean {
        return id == correctAnswer.id
    }
}