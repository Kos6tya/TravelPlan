package com.example.travelplan.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.travelplan.domain.Trip

@Composable
fun HomeScreen(viewModel: TripViewModel) {
    // Спостерігаємо за даними з ViewModel
    val trips by viewModel.allTrips.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Пізніше додамо екран додавання */ }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(trips) { trip ->
                ListItem(
                    headlineContent = { Text(trip.title) },
                    supportingContent = { Text(trip.destination) }
                )
            }
        }
    }
}