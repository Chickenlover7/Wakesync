package com.example.data

data class FatigueState(
    val needsBreak: Boolean,
    val reason: String?
)

object FatigueEngine {
    /**
     * Evaluates whether the user needs a break based on continuous task completion.
     * @param tasks Array of the user's tasks
     * @param maxContinuousWorkHours Threshold for burnout (default: 3.5 hours)
     * @return FatigueState indicating if a nap/break is required
     */
    fun evaluateCognitiveFatigue(
        tasks: List<Task>,
        maxContinuousWorkHours: Double = 3.5
    ): FatigueState {
        val todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // simplify check to last 24h for active tasks
        
        // Filter for tasks completed today and sort them chronologically
        val completedTasks = tasks
            .filter { it.isCompleted && it.completedAt != null && it.completedAt > todayStart }
            .sortedBy { it.completedAt }

        if (completedTasks.size < 2) {
            return FatigueState(needsBreak = false, reason = null)
        }

        // Find the time of the first task completed in the current continuous sprint
        val firstTaskTime = completedTasks.first().completedAt!!
        val lastTaskTime = completedTasks.last().completedAt!!

        // Calculate the difference in hours
        val hoursWorked = (lastTaskTime - firstTaskTime) / (1000.0 * 60 * 60)

        if (hoursWorked >= maxContinuousWorkHours) {
            val formattedHours = String.format("%.1f", hoursWorked)
            return FatigueState(
                needsBreak = true,
                reason = "You've been actively completing tasks for $formattedHours hours. A 20-minute nap will restore your focus."
            )
        }

        return FatigueState(needsBreak = false, reason = null)
    }
}
