package io.github.wonshaw.dogceodemo.domain.usecase

sealed class UseCaseResult<out T> {
    data class Success<out T>(val data: T) : UseCaseResult<T>()
    data class Error(val message: String = "Error", val exception: Exception? = null) : UseCaseResult<Nothing>()
}