package com.example.trip.models

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    class Failure<out T>(val errorCode: Int = 404) : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
}
