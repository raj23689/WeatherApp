package com.example.weathersample.ui.content.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.weathersample.data.details.WeatherDetailResponse
import com.example.weathersample.ui.content.UiState

@Composable
fun WeatherDetailsScreen(
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.weatherDetails.collectAsStateWithLifecycle()

    WeatherDetailsScreenContent(
        uiState = uiState
    )
}

@Composable
fun WeatherDetailsScreenContent(uiState: UiState<WeatherDetailResponse>) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Idle -> {
                    Text("Waiting for city input...", style = MaterialTheme.typography.bodyLarge)
                }

                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    val message = uiState.message
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is UiState.Success -> {
                    val weather = uiState.data

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        val iconUrl = "https:${weather.current.condition.icon}"
                        SubcomposeAsyncImage(
                            model = iconUrl,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(64.dp)
                                )
                            },
                            contentDescription = "Weather Icon",
                            modifier = Modifier
                                .size(64.dp)
                        )
                        Text(
                            text = "${weather.location.name}, ${weather.location.country}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Temperature: ${weather.current.tempC} °C",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Condition: ${weather.current.condition.text}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Humidity: ${weather.current.humidity} %",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Feels Like: ${weather.current.feelslikeC} °C",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}