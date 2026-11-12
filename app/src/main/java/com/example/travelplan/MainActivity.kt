package com.example.travelplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelplan.data.TripRepository
import com.example.travelplan.data.local.AppDatabase
import com.example.travelplan.ui.HomeScreen
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

            HomeScreen(viewModel)
        }
    }
}