package com.example.travelplan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

data class MapPlace(
    val title: String,
    val position: LatLng
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    tripTitle: String,
    destination: String,
    onBackClick: () -> Unit
) {
    val defaultLocation = remember(destination) {
        when {
            destination.contains("Рим", ignoreCase = true) || destination.contains("Rome", ignoreCase = true) -> LatLng(41.9028, 12.4964)
            destination.contains("Париж", ignoreCase = true) || destination.contains("Paris", ignoreCase = true) -> LatLng(48.8566, 2.3522)
            destination.contains("Київ", ignoreCase = true) || destination.contains("Kyiv", ignoreCase = true) -> LatLng(50.4501, 30.5234)
            destination.contains("Лондон", ignoreCase = true) || destination.contains("London", ignoreCase = true) -> LatLng(51.5074, -0.1278)
            destination.contains("Берлін", ignoreCase = true) || destination.contains("Berlin", ignoreCase = true) -> LatLng(52.5200, 13.4050)
            else -> LatLng(50.4501, 30.5234)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val markers = remember { mutableStateListOf<MapPlace>() }
    var showAddPlaceDialog by remember { mutableStateOf<LatLng?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карта: $tripTitle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("⬅️", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    showAddPlaceDialog = latLng
                }
            ) {
                Marker(
                    state = MarkerState(position = defaultLocation),
                    title = destination,
                    snippet = "Головна точка подорожі"
                )

                markers.forEach { place ->
                    Marker(
                        state = MarkerState(position = place.position),
                        title = place.title,
                        snippet = "Збережене місце"
                    )
                }
            }

            Card(
                modifier = Modifier
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "💡 Натисніть на карті, щоб додати мітку!",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        showAddPlaceDialog?.let { coords ->
            var placeName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddPlaceDialog = null },
                title = { Text("Нове місце на карті") },
                text = {
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Назва (напр. Готель, Музей)") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (placeName.isNotBlank()) {
                                markers.add(MapPlace(placeName, coords))
                                showAddPlaceDialog = null
                            }
                        }
                    ) {
                        Text("Додати")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddPlaceDialog = null }) {
                        Text("Скасувати")
                    }
                }
            )
        }
    }
}