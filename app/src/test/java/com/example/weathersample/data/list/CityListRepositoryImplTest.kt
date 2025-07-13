package com.example.weathersample.data.list

import com.example.weathersample.data.WeatherApiService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CityListRepositoryImplTest {

    private lateinit var service: WeatherApiService
    private lateinit var repository: CityListRepositoryImpl

    @Before
    fun setUp() {
        service = mockk()
        repository = CityListRepositoryImpl(api = service)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `searchForCity returns valid response for a valid query`() = runTest {
        val sampleResponse = listOf(
            CitySearchResponseItem(name = "London", country = "UK"),
            CitySearchResponseItem(name = "Paris", country = "France")
        )
        coEvery { service.searchForCity(query = "Lon") } returns Response.success(
            sampleResponse
        )
        // Act
        val response = repository.searchForCity("Lon")

        // Assert
        assertNotNull(response)
        assertTrue(response.isSuccessful)
        assertEquals(sampleResponse, response.body())
    }

    @Test
    fun `searchForCity returns empty response for a query with no matches`() = runTest {
        // Arrange
        val sampleResponse = emptyList<CitySearchResponseItem>()
        coEvery { service.searchForCity(query = "Unknown") } returns Response.success(
            sampleResponse
        )

        // Act
        val response = repository.searchForCity("Unknown")

        // Assert
        assertNotNull(response)
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.isEmpty() == true)
    }

    @Test
    fun `searchForCity handles API error gracefully`() = runTest {
        coEvery {
            service.searchForCity(query = "ErrorCity")
        } returns Response.error(404, "".toResponseBody(null))

        // Act
        val response = repository.searchForCity("ErrorCity")

        // Assert
        assertNotNull(response)
        assertEquals(404, response.code())
    }
}