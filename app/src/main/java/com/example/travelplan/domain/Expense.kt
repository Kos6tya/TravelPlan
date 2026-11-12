package com.example.travelplan.domain

data class Expense(
    val id: String = "",
    val tripId: String = "",
    val category: String,
    val amount: Double,
    val description: String = ""
)