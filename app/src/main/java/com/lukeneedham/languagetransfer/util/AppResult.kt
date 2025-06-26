package com.lukeneedham.languagetransfer.util

sealed interface AppResult<T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure<T>(val error: Throwable) : AppResult<T>

    companion object {
        fun <T> Failure(message: String) = Failure<T>(Exception(message))
    }
}