package com.example.weathersample.data.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDetailResponse(
    val current: Current = Current(),
    val location: Location = Location()
) {
    @Serializable
    data class Current(
        @SerialName("cloud")
        val cloud: Int = 0,
        @SerialName("condition")
        val condition: Condition = Condition(),
        @SerialName("dewpoint_c")
        val dewpointC: Double = 0.0,
        @SerialName("dewpoint_f")
        val dewpointF: Double = 0.0,
        @SerialName("feelslike_c")
        val feelslikeC: Double = 0.0,
        @SerialName("feelslike_f")
        val feelslikeF: Double = 0.0,
        @SerialName("gust_kph")
        val gustKph: Double = 0.0,
        @SerialName("gust_mph")
        val gustMph: Double = 0.0,
        @SerialName("heatindex_c")
        val heatindexC: Double = 0.0,
        @SerialName("heatindex_f")
        val heatindexF: Double = 0.0,
        @SerialName("humidity")
        val humidity: Int = 0,
        @SerialName("is_day")
        val isDay: Int = 0,
        @SerialName("last_updated")
        val lastUpdated: String = "",
        @SerialName("last_updated_epoch")
        val lastUpdatedEpoch: Int = 0,
        @SerialName("precip_in")
        val precipIn: Double = 0.0,
        @SerialName("precip_mm")
        val precipMm: Double = 0.0,
        @SerialName("pressure_in")
        val pressureIn: Double = 0.0,
        @SerialName("pressure_mb")
        val pressureMb: Double = 0.0,
        @SerialName("temp_c")
        val tempC: Double = 0.0,
        @SerialName("temp_f")
        val tempF: Double = 0.0,
        @SerialName("uv")
        val uv: Double = 0.0,
        @SerialName("vis_km")
        val visKm: Double = 0.0,
        @SerialName("vis_miles")
        val visMiles: Double = 0.0,
        @SerialName("wind_degree")
        val windDegree: Int = 0,
        @SerialName("wind_dir")
        val windDir: String = "",
        @SerialName("wind_kph")
        val windKph: Double = 0.0,
        @SerialName("wind_mph")
        val windMph: Double = 0.0,
        @SerialName("windchill_c")
        val windchillC: Double = 0.0,
        @SerialName("windchill_f")
        val windchillF: Double = 0.0
    ) {
        @Serializable
        data class Condition(
            @SerialName("code")
            val code: Int = 0,
            @SerialName("icon")
            val icon: String = "",
            @SerialName("text")
            val text: String = ""
        )
    }

    @Serializable
    data class Location(
        @SerialName("country")
        val country: String = "",
        @SerialName("lat")
        val lat: Double = 0.0,
        @SerialName("localtime")
        val localtime: String = "",
        @SerialName("localtime_epoch")
        val localtimeEpoch: Int = 0,
        @SerialName("lon")
        val lon: Double = 0.0,
        @SerialName("name")
        val name: String = "",
        @SerialName("region")
        val region: String = "",
        @SerialName("tz_id")
        val tzId: String = ""
    )
}
