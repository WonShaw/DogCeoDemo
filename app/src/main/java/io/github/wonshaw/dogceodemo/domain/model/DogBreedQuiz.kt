package io.github.wonshaw.dogceodemo.domain.model

import android.graphics.Bitmap

class DogBreedQuiz(
    val imageBitmap: Bitmap,
    val options: List<DogBreed>,
    val correctAnswer: DogBreed,
) {
    fun isCorrectAnswer(id: String): Boolean {
        return id == correctAnswer.id
    }
}