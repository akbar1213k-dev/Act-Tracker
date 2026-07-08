package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ActivityEntity
import com.example.data.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val repository: ActivityRepository) : ViewModel() {

    val uiState: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun parseAndSaveData(inputText: String) {
        if (inputText.isBlank()) return

        val lines = inputText.split("\n")
        val regex = """\[?(\d{1,2}/\d{1,2}(?:/\d{2,4})?)[, ]+(\d{2}[.:]\d{2}(?:[.:]\d{2})?)\]?\s+(.*?):\s+(.*)""".toRegex()

        val activities = mutableListOf<ActivityEntity>()
        var currentActivity: TempActivity? = null

        for (line in lines) {
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val (date, time, _, messageMatch) = matchResult.destructured
                val message = messageMatch.trim()
                val isEndMarker = message.matches("""^\.+$""".toRegex())

                // Close previous activity if it exists
                if (currentActivity != null) {
                    val durInfo = calculateDurationInfo(currentActivity.time, time)
                    activities.add(
                        ActivityEntity(
                            id = currentActivity.id,
                            date = currentActivity.date,
                            startTime = currentActivity.time,
                            endTime = time,
                            activity = currentActivity.message,
                            durationText = durInfo.text,
                            rawMinutes = durInfo.rawMinutes
                        )
                    )
                }

                // Start new activity if not an end marker
                currentActivity = if (!isEndMarker) {
                    TempActivity(id = UUID.randomUUID().toString(), date = date, time = time, message = message)
                } else {
                    null
                }
            }
        }

        if (activities.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertActivities(activities)
            }
        }
    }

    private data class TempActivity(val id: String, val date: String, val time: String, val message: String)

    private fun calculateDurationInfo(startTime: String, endTime: String): DurationInfo {
        val start = startTime.replace('.', ':')
        val end = endTime.replace('.', ':')
        
        val startParts = start.split(":")
        val startHour = startParts.getOrNull(0)?.toIntOrNull() ?: 0
        val startMin = startParts.getOrNull(1)?.toIntOrNull() ?: 0
        
        val endParts = end.split(":")
        val endHour = endParts.getOrNull(0)?.toIntOrNull() ?: 0
        val endMin = endParts.getOrNull(1)?.toIntOrNull() ?: 0

        val startTotalMinutes = startHour * 60 + startMin
        var endTotalMinutes = endHour * 60 + endMin

        if (endTotalMinutes < startTotalMinutes) {
            endTotalMinutes += 24 * 60
        }

        val diffMinutes = endTotalMinutes - startTotalMinutes
        val hours = diffMinutes / 60
        val minutes = diffMinutes % 60

        val text = when {
            hours > 0 && minutes > 0 -> "${hours}j ${minutes}m"
            hours > 0 -> "${hours}j"
            else -> "${minutes}m"
        }

        return DurationInfo(text, diffMinutes)
    }

    private data class DurationInfo(val text: String, val rawMinutes: Int)
}

class MainViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
