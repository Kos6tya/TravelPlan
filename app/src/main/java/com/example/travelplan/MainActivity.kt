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
import com.example.travelplan.ui.AuthViewModel
import com.example.travelplan.ui.ChatScreen
import com.example.travelplan.ui.HomeScreen
import com.example.travelplan.ui.LoginScreen
import com.example.travelplan.ui.MapScreen
import com.example.travelplan.ui.TripViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val repository = TripRepository(db.tripDao())

        setContent {
            val tripViewModel: TripViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return TripViewModel(repository) as T
                }
            })

            val authViewModel: AuthViewModel = viewModel()
            val navController = rememberNavController()

            val startDestination = if (authViewModel.currentUser != null) "home" else "login"

            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onAuthSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("home") {
                    HomeScreen(
                        viewModel = tripViewModel,
                        onTripClick = { trip ->
                            tripViewModel.selectTrip(trip)
                            navController.navigate("map")
                        },
                        onAnalyticsClick = { trip ->
                            tripViewModel.selectTrip(trip)
                            navController.navigate("analytics")
                        },
                        onChatClick = { trip ->
                            tripViewModel.selectTrip(trip)
                            navController.navigate("chat")
                        },
                        onLogoutClick = {
                            authViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    )
                }
                composable("map") {
                    val trip = tripViewModel.selectedTrip
                    if (trip != null) {
                        MapScreen(
                            tripTitle = trip.title,
                            destination = trip.destination,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                composable("analytics") {
                    val trip = tripViewModel.selectedTrip
                    if (trip != null) {
                        AnalyticsScreen(
                            tripTitle = trip.title,
                            totalBudget = trip.budget,
                            viewModel = tripViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                composable("chat") {
                    val trip = tripViewModel.selectedTrip
                    val email = authViewModel.currentUser?.email ?: "Користувач"
                    if (trip != null) {
                        ChatScreen(
                            tripTitle = trip.title,
                            currentUserEmail = email,
                            viewModel = tripViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}