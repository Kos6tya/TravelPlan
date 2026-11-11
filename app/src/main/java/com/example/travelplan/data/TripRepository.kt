package com.example.travelplan.data

import com.example.travelplan.data.local.TripDao
import com.example.travelplan.domain.Trip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TripRepository(
    private val tripDao: TripDao
) {
    private val firebaseDb = FirebaseDatabase.getInstance().getReference("trips")

    fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun saveTrip(trip: Trip) {
        val tripId = trip.id.ifEmpty { firebaseDb.push().key ?: return }
        val tripToSave = trip.copy(id = tripId)

        tripDao.insertTrip(tripToSave.toEntityModel(isSynced = false))

        try {
            firebaseDb.child(tripId).setValue(tripToSave).await()
            tripDao.insertTrip(tripToSave.toEntityModel(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip.toEntityModel())

        try {
            firebaseDb.child(trip.id).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}