package com.example.weathersample.data

import org.junit.Assert
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Test class for utility methods in UtilsKt.
 * This class tests the functionality of the getApiResponse function.
 */
class UtilsKtTest {

    @Test
    fun `getApiResponse should return successful response if API call succeeds`() {
        val expectedResponse = Response.success("Successful Response")
        val apiCall = { expectedResponse }

        val actualResponse = getApiResponse(apiCall)

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getApiResponse should return error response with 500 status code on IOException`() {
        val apiCall = { throw IOException("Simulated network error") }

        val actualResponse = getApiResponse<String>(apiCall)

        Assert.assertEquals(500, actualResponse.code())
        Assert.assertEquals(
            "Network error: Simulated network error",
            actualResponse.errorBody()?.string()
        )
    }

    @Test
    fun `getApiResponse should return error response when API call throws a RuntimeException`() {
        val apiCall = { throw RuntimeException("Unexpected exception") }

        val actualResponse = getApiResponse<String>(apiCall)

        Assert.assertEquals(500, actualResponse.code())
        Assert.assertEquals(
            "Runtime error: Unexpected exception",
            actualResponse.errorBody()?.string()
        )
    }
}