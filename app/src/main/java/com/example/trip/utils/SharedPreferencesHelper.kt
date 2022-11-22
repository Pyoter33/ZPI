package com.example.trip.utils

import android.content.Context
import com.example.trip.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun getUserId(): Long {
        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getLong(Constants.USER_ID_KEY, -1L)
    }

    fun getToken(): String {
        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(Constants.AUTHORIZATION_HEADER, "")!!
    }

    fun setUserId(id: Long) {
        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putLong(Constants.USER_ID_KEY, id).apply()
    }

    fun setToken(token: String) {
        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(Constants.AUTHORIZATION_HEADER, token).apply()
    }

    fun containsKey(key: String): Boolean {
        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.contains(key)
    }

}