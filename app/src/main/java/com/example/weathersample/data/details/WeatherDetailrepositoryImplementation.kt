package com.example.weathersample.data.details

import com.example.weathersample.data.WeatherApiService
import com.example.weathersample.data.getApiResponse
import retrofit2.Response

class WeatherDetailRepositoryImpl(
    private val api: WeatherApiService
): WeatherDetailRepository {
    override suspend fun getWeatherDetails(cityName: String): Response<WeatherDetailResponse> {
        return getApiResponse {
            api.getWeatherDetails(city = cityName)
        }
    }
}