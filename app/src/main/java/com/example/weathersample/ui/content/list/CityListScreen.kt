package com.example.weathersample.ui.content.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weathersample.data.list.CitySearchResponseItem
import com.example.weathersample.ui.content.UiState

@Composable
fun CityListScreen(
    onCityClick: (String) -> Unit
) {
    val viewModel: CityListViewModel = hiltViewModel()
    val citySearchQuery by viewModel.cityQuery.collectAsStateWithLifecycle()
    val cityListUiState by viewModel.cityList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is CityListViewModel.CityListUiEvent.OnCitySelected -> {
                    onCityClick(event.cityName)
                }
            }
        }
    }

    CityListScreenContent(
        cityListUiState = cityListUiState,
        citySearchQuery = citySearchQuery,
        onAction = viewModel::onAction
    )
}

@Composable
fun CityListScreenContent(
    cityListUiState: UiState<List<CitySearchResponseItem>>,
    citySearchQuery: String,
    onAction: (CityListUiAction) -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = citySearchQuery,
                onValueChange = {
                    onAction(CityListUiAction.OnQueryChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = {
                    Text("Search city...")
                },
                label = {
                    Text("City name")
                },
                singleLine = true,
                trailingIcon = if (cityListUiState is UiState.Loading) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else null,
            )

            when (cityListUiState) {
                UiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Start searching for cities.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    val message = cityListUiState.message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is UiState.Success -> {
                    LazyColumn {
                        items(
                            items = cityListUiState.data,
                            key = { it.id!! }
                        ) {
                            ListItem(
                                modifier = Modifier.clickable {
                                    onAction(
                                        CityListUiAction.OnCitySelected(
                                            it.name
                                        )
                                    )
                                },
                                headlineContent = {
                                    it.name?.let { text ->
                                        Text(
                                            text = text,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

sealed interface CityListUiAction {
    data class OnQueryChange(val query: String) : CityListUiAction
    data class OnCitySelected(val cityName: String?) : CityListUiAction
}