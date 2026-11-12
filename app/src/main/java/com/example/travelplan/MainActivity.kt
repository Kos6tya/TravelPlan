package com.example.travelplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travelplan.data.TripRepository
import com.example.travelplan.data.local.AppDatabase
import com.example.travelplan.ui.AnalyticsScreen
import com.example.travelplan.ui.HomeScreen
import com.example.travelplan.ui.MapScreen
import com.example.travelplan.ui.TripViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val repository = TripRepository(db.tripDao())

        setContent {
            val viewModel: TripViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return TripViewModel(repository) as T
                }
            })

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onTripClick = { trip ->
                            viewModel.selectTrip(trip)
                            navController.navigate("map")
                        },
                        onAnalyticsClick = { trip ->
                            viewModel.selectTrip(trip)
                            navController.navigate("analytics")
                        }
                    )
                }
                composable("map") {
                    val trip = viewModel.selectedTrip
                    if (trip != null) {
                        MapScreen(
                            tripTitle = trip.title,
                            destination = trip.destination,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                composable("analytics") {
                    val trip = viewModel.selectedTrip
                    if (trip != null) {
                        AnalyticsScreen(
                            tripTitle = trip.title,
                            totalBudget = trip.budget,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}