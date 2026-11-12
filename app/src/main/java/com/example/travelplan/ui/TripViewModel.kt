package com.example.travelplan.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplan.data.TripRepository
import com.example.travelplan.domain.Expense
import com.example.travelplan.domain.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TripRepository) : ViewModel() {

    val allTrips = repository.getAllTrips().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var selectedTrip by mutableStateOf<Trip?>(null)
        private set

    val expenses = mutableStateListOf(
        Expense(category = "Готель", amount = 450.0),
        Expense(category = "Авіаквитки", amount = 350.0),
        Expense(category = "Ресторани та їжа", amount = 200.0),
        Expense(category = "Музеї та екскурсії", amount = 120.0)
    )

    fun selectTrip(trip: Trip) {
        selectedTrip = trip
    }

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            repository.saveTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    fun deleteExpense(expense: Expense) {
        expenses.remove(expense)
    }
}