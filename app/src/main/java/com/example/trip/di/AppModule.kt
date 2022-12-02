package com.example.trip.di

import android.content.Context
import android.content.pm.PackageManager
import com.example.trip.Constants
import com.example.trip.dto.AirTransportDto
import com.example.trip.dto.CarTransportDto
import com.example.trip.dto.TransportDto
import com.example.trip.dto.UserTransportDto
import com.example.trip.service.*
import com.example.trip.utils.moshi.*
import com.google.maps.GeoApiContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.onenowy.moshipolymorphicadapter.ValuePolymorphicAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named(Constants.BASE_URL)
    fun provideUrl() = Constants.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        val headerInterceptor = HeaderInterceptor(context)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        val transportFactory = ValuePolymorphicAdapterFactory.of(TransportDto::class.java, "transportTypeJson", Int::class.java)
            .withSubtype(AirTransportDto::class.java, 1)
            .withSubtype(CarTransportDto::class.java, 2)
            .withSubtype(UserTransportDto::class.java, 3)

        return Moshi.Builder()
            .add(LocalDateAdapter())
            .add(CurrencyAdapter())
            .add(BigDecimalAdapter())
            .add(LocalDateTimeAdapter())
            .add(DurationAdapter())
            .add(transportFactory)
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, @Named(Constants.BASE_URL) baseUrl: String, moshiConverterFactory: MoshiConverterFactory): Retrofit = Retrofit.Builder()
        .addConverterFactory(moshiConverterFactory)
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi).withNullSerialization()

    @Provides
    @Named(Constants.GOOGLE_API_KEY)
    fun provideApiKey(@ApplicationContext context: Context): String? {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        return appInfo.metaData.getString(Constants.GOOGLE_API_KEY)
    }

    @Provides
    @Singleton
    fun provideGeoApiContext(@Named(Constants.GOOGLE_API_KEY) apiKey: String?): GeoApiContext {
        return GeoApiContext.Builder().apiKey(apiKey)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccommodationService(retrofit: Retrofit): AccommodationService {
        return retrofit.create(AccommodationService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideDayPlanService(retrofit: Retrofit): DayPlanService {
        return retrofit.create(DayPlanService::class.java)
    }

    @Provides
    @Singleton
    fun provideAvailabilityService(retrofit: Retrofit): AvailabilityService {
        return retrofit.create(AvailabilityService::class.java)
    }


    @Provides
    @Singleton
    fun provideTripGroupService(retrofit: Retrofit): TripGroupService {
        return retrofit.create(TripGroupService::class.java)
    }

    @Provides
    @Singleton
    fun provideFinanceService(retrofit: Retrofit): FinanceService {
        return retrofit.create(FinanceService::class.java)
    }

    @Provides
    @Singleton
    fun provideTransportService(retrofit: Retrofit): TransportService {
        return retrofit.create(TransportService::class.java)
    }

}