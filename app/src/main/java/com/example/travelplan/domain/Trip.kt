package com.example.travelplan.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val id: String = "",
    val title: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val isSynced: Boolean = false
) : Parcelable