package com.example.travelplan.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplan.data.TripRepository
import com.example.travelplan.domain.ChatMessage
import com.example.travelplan.domain.Expense
import com.example.travelplan.domain.Trip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    val chatMessages = mutableStateListOf<ChatMessage>()

    private val firebaseDb = FirebaseDatabase.getInstance("https://travelplan-d045e-default-rtdb.europe-west1.firebasedatabase.app")

    fun selectTrip(trip: Trip) {
        selectedTrip = trip
        loadChatMessages(trip.id)
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

    private fun loadChatMessages(tripId: String) {
        val chatRef = firebaseDb.getReference("chats").child(tripId)
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatMessages.clear()
                for (child in snapshot.children) {
                    val msg = child.getValue(ChatMessage::class.java)
                    if (msg != null) {
                        chatMessages.add(msg)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun sendMessage(text: String, senderEmail: String) {
        val tripId = selectedTrip?.id ?: return
        if (text.isBlank()) return

        val chatRef = firebaseDb.getReference("chats").child(tripId)
        val msgId = chatRef.push().key ?: return

        val newMessage = ChatMessage(
            id = msgId,
            senderEmail = senderEmail,
            messageText = text,
            timestamp = System.currentTimeMillis()
        )

        chatRef.child(msgId).setValue(newMessage)
    }
}