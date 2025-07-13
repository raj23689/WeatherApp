package com.example.weathersample.ui.content.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersample.data.details.WeatherDetailRepository
import com.example.weathersample.data.details.WeatherDetailResponse
import com.example.weathersample.ui.content.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: WeatherDetailRepository
) : ViewModel() {

    companion object {
        const val KEY_CITY_NAME = "city_name"
        private const val ERROR_NO_DATA = "No data found"
        private const val ERROR_UNKNOWN = "Unknown error"
    }

    private val cityName = savedStateHandle.getStateFlow(KEY_CITY_NAME, "")

    val weatherDetails: StateFlow<UiState<WeatherDetailResponse>> =
        cityName
            .flatMapLatest { cityName ->
                fetchWeatherDetails(cityName)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UiState.Loading
            )

    private fun fetchWeatherDetails(cityName: String) = flow {
        emit(UiState.Loading)
        val response = repository.getWeatherDetails(cityName)
        if (response.isSuccessful) {
            response.body()?.let { weatherDetail ->
                emit(UiState.Success(weatherDetail))
            } ?: emit(UiState.Error(ERROR_NO_DATA))
        } else {
            emit(UiState.Error(response.errorBody()?.string() ?: ERROR_UNKNOWN))
        }
    }
}