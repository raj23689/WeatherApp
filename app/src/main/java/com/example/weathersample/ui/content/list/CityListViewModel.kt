package com.example.weathersample.ui.content.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersample.data.list.CityListRepository
import com.example.weathersample.data.list.CitySearchResponseItem
import com.example.weathersample.ui.content.UiState
//import com.example.weathersample.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityListRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val cityQuery = savedStateHandle.getStateFlow(CITY_QUERY_KEY, "")

    private val _eventChannel = Channel<CityListUiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    val cityList: StateFlow<UiState<List<CitySearchResponseItem>>> = cityQuery
        .debounce(DEBOUNCE_TIMEOUT_MS)
        .distinctUntilChanged()
        .flatMapLatest { query -> searchCities(query) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Idle
        )

    private fun searchCities(query: String) = flow {
        if (query.isBlank()) {
            emit(UiState.Idle)
            return@flow
        }
        emit(UiState.Loading)
        val response = repository.searchForCity(query)
        if (response.isSuccessful) {
            response.body()?.let {
                emit(UiState.Success(it))
            } ?: emit(UiState.Error("No data found"))
        } else {
            emit(UiState.Error(response.errorBody()?.string() ?: "Unknown error"))
        }
    }

    private fun updateCityQuery(query: String) {
        savedStateHandle[CITY_QUERY_KEY] = query
    }

    fun onAction(uiAction: CityListUiAction) {
        when (uiAction) {
            is CityListUiAction.OnQueryChange -> updateCityQuery(uiAction.query)
            is CityListUiAction.OnCitySelected -> sendEvent(CityListUiEvent.OnCitySelected(uiAction.cityName.toString()))
        }
    }

    private fun sendEvent(event: CityListUiEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    companion object {
        private const val CITY_QUERY_KEY = "city_query"
        private const val DEBOUNCE_TIMEOUT_MS = 300L
    }

    sealed interface CityListUiEvent {
        data class OnCitySelected(val cityName: String) : CityListUiEvent
    }
}
