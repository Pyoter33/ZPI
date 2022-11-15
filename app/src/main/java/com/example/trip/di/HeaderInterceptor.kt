package com.example.trip.di

import android.content.Context
import com.example.trip.Constants
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val preferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        val token = preferences.getString(Constants.AUTHORIZATION_HEADER, "")!!

        val newRequest = originalRequest.newBuilder()
            .header(Constants.AUTHORIZATION_HEADER, token)
            .build()

        return chain.proceed(newRequest)
    }


}
