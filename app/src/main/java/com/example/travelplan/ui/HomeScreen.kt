package com.example.travelplan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.travelplan.domain.Trip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TripViewModel) {
    val trips by viewModel.allTrips.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мої Подорожі 🌍") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Поки немає подорожей. Натисніть +, щоб додати!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trips, key = { it.id }) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text(trip.title, style = MaterialTheme.typography.titleMedium) },
                            supportingContent = {
                                Text("${trip.destination} • Бюджет: $${trip.budget}")
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteTrip(trip) }) {
                                    Text("🗑", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Вікно додавання нової подорожі
        if (showDialog) {
            AddTripDialog(
                onDismiss = { showDialog = false },
                onConfirm = { newTrip ->
                    viewModel.addTrip(newTrip)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddTripDialog(
    onDismiss: () -> Unit,
    onConfirm: (Trip) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова Подорож") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва (напр. Літо в Італії)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Місто / Країна (напр. Рим)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Бюджет ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && destination.isNotBlank()) {
                        onConfirm(
                            Trip(
                                title = title,
                                destination = destination,
                                startDate = System.currentTimeMillis(),
                                endDate = System.currentTimeMillis() + 86400000 * 7,
                                budget = budget.toDoubleOrNull() ?: 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}