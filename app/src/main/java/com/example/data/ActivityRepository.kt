package com.example.data

import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    val allActivities: Flow<List<ActivityEntity>> = activityDao.getAllActivities()

    suspend fun insertActivities(activities: List<ActivityEntity>) {
        activityDao.insertActivities(activities)
    }
}
