package com.example.weathersample.data.list


import kotlinx.serialization.Serializable

@Serializable
data class CitySearchResponseItem(
    val name: String = "",
    val id: Int = 0,
    val region: String = "",
    val country: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val url: String = ""
)