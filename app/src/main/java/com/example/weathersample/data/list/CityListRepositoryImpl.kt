package com.example.weathersample.data.list

import com.example.weathersample.data.WeatherApiService
import com.example.weathersample.data.getApiResponse
import retrofit2.Response


class CityListRepositoryImpl(
    private val api: WeatherApiService
): CityListRepository {
    override suspend fun searchForCity(query: String): Response<List<CitySearchResponseItem>> {
        return getApiResponse {
            api.searchForCity(query = query)
        }
    }
}