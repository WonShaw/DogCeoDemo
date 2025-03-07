package io.github.wonshaw.dogceodemo.presentation.quiz

data class QuizOptions(
    val id: String,
    val displayText: String,
    val shaking: Boolean = false,
)