package com.example.oroiapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oroiapp.model.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    // Harpidetza guztiak lortzen ditu, izenaren arabera ordenatuta
    @Query("SELECT * FROM subscriptions ORDER BY name ASC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    // Harpidetza bat txertatzen edo ordezkatzen du
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: Subscription)

    // Harpidetza bat ezabatzen du
    @Delete
    suspend fun delete(subscription: Subscription)
}