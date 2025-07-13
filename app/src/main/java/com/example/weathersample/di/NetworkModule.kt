package com.example.weathersample.di

import com.example.weathersample.data.WeatherApiService
import com.example.weathersample.data.details.WeatherDetailRepository
import com.example.weathersample.data.details.WeatherDetailRepositoryImpl
import com.example.weathersample.data.list.CityListRepository
import com.example.weathersample.data.list.CityListRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCityListRepository(api: WeatherApiService): CityListRepository {
        return CityListRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideWeatherDetailRepository(api: WeatherApiService): WeatherDetailRepository {
        return WeatherDetailRepositoryImpl(api)
    }
}