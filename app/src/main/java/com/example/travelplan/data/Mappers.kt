package com.example.travelplan.data

import com.example.travelplan.data.local.TripEntity
import com.example.travelplan.domain.Trip

fun TripEntity.toDomainModel(): Trip {
    return Trip(
        id = this.id,
        title = this.title,
        destination = this.destination,
        startDate = this.startDate,
        endDate = this.endDate,
        budget = this.budget,
        isSynced = this.isSynced
    )
}

fun Trip.toEntityModel(isSynced: Boolean = this.isSynced): TripEntity {
    return TripEntity(
        id = this.id,
        title = this.title,
        destination = this.destination,
        startDate = this.startDate,
        endDate = this.endDate,
        budget = this.budget,
        isSynced = isSynced
    )
}