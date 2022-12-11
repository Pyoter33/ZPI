package com.example.trip.models

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>() {
        override fun equals(other: Any?): Boolean {
            return other is Loading<*>
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class Failure<out T>(val errorCode: Int = 404, val message: String? = null) : Resource<T>() {
        override fun equals(other: Any?): Boolean {
            return other is Failure<*> && other.errorCode == errorCode
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
    data class Success<out T>(val data: T) : Resource<T>()
}
