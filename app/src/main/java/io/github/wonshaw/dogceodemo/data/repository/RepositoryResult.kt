package io.github.wonshaw.dogceodemo.data.repository

sealed class RepositoryResult<out T> {
    data class Success<out T>(val data: T) : RepositoryResult<T>()
    data class Error(val message: String = "Error", val exception: Exception? = null) : RepositoryResult<Nothing>()
}