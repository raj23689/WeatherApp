package com.example.weathersample.data

import com.example.weathersample.data.details.WeatherDetailResponse
import com.example.weathersample.data.list.CitySearchResponseItem
import com.example.weathersample.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/search.json")
    suspend fun searchForCity(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("q") query: String
    ): Response<List<CitySearchResponseItem>>

    @GET("v1/current.json")
    suspend fun getWeatherDetails(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("q") city: String
    ): Response<WeatherDetailResponse>

}