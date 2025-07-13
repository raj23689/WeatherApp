package com.example.weathersample.data.details

import retrofit2.Response

interface WeatherDetailRepository {
    suspend fun getWeatherDetails(cityName: String): Response<WeatherDetailResponse>
}