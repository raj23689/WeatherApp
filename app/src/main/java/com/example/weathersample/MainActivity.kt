package com.example.weathersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersample.ui.content.detail.WeatherDetailsScreen
import com.example.weathersample.ui.content.detail.WeatherViewModel
import com.example.weathersample.ui.content.list.CityListScreen
import com.example.weathersample.ui.theme.WeatherSampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSampleTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        CityListScreen {
                            navController.navigate("detail/$it")
                        }
                    }

                    composable("detail/{${WeatherViewModel.KEY_CITY_NAME}}") {
                        val viewModel: WeatherViewModel = hiltViewModel()
                        WeatherDetailsScreen(viewModel)
                    }
                }
            }
        }
    }
}