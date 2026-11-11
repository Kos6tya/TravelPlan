package com.example.travelplan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips_table")
data class TripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val isSynced: Boolean
)