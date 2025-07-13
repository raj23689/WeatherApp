package com.example.weathersample.data

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

inline fun <T> getApiResponse(apiCall: () -> Response<T>): Response<T> {
    return try {
        apiCall()
    } catch (e: IOException) {
        Response.error(500, "Network error: ${e.localizedMessage}".toResponseBody(null))
    } catch (e: RuntimeException) {
        Response.error(500, "Runtime error: ${e.localizedMessage}".toResponseBody(null))
    }
}