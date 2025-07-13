package com.example.weathersample.data.detail

import com.example.weathersample.data.WeatherApiService
import com.example.weathersample.data.details.WeatherDetailRepositoryImpl
import com.example.weathersample.data.details.WeatherDetailResponse
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WeatherDetailRepositoryImplTest {

    private lateinit var weatherApiService: WeatherApiService
    private lateinit var weatherDetailRepository: WeatherDetailRepositoryImpl

    @Before
    fun setUp() {
        weatherApiService = mockk()
        weatherDetailRepository = WeatherDetailRepositoryImpl(api = weatherApiService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getWeatherDetails returns successful response`() = runTest {
        // Arrange
        val cityName = "London"
        val responseMock = mockk<Response<WeatherDetailResponse>>(relaxed = true)
        coEvery { weatherApiService.getWeatherDetails(city = cityName) } returns responseMock

        // Act
        val result = weatherDetailRepository.getWeatherDetails(cityName)

        // Assert
        coVerify { weatherApiService.getWeatherDetails(city = cityName) }
        assertNotNull(result)
        assertEquals(responseMock, result)
    }

    @Test
    fun `getWeatherDetails handles API failure`() = runTest {
        // Arrange
        val cityName = "InvalidCity"
        val responseMock = mockk<Response<WeatherDetailResponse>>()
        coEvery { weatherApiService.getWeatherDetails(city = cityName) } returns responseMock

        // Act
        val result = weatherDetailRepository.getWeatherDetails(cityName)

        // Assert
        coVerify { weatherApiService.getWeatherDetails(city = cityName) }
        assertNotNull(result)
        assertEquals(responseMock, result)
    }


    @Test
    fun `getWeatherDetails handles empty city name`() = runTest {
        // Arrange
        val cityName = ""
        coEvery { weatherApiService.getWeatherDetails(city = cityName) } returns Response.error(
            404,
            "".toResponseBody(null)
        )

        // Act
        val response = weatherDetailRepository.getWeatherDetails(cityName)

        // Assert
        coVerify { weatherApiService.getWeatherDetails(city = cityName) }
        assertNotNull(response)
        assertEquals(404, response.code())
    }
}