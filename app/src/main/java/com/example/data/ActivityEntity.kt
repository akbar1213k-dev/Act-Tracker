package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val activity: String,
    val durationText: String,
    val rawMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
