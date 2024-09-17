package com.example.todo_mad.utill

import android.content.Context

object SharedPreferencesHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val LAST_TASK_ID_KEY = "last_task_id"

    // Save timer settings or state
    fun saveTimerSettings(isRunning: Boolean, lastRecordedTime: Long, context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("timer_running", isRunning)
        editor.putLong("last_recorded_time", lastRecordedTime)
        editor.apply()
    }

    // Load timer settings or state
    fun loadTimerSettings(context: Context): Pair<Boolean, Long> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isRunning = sharedPreferences.getBoolean("timer_running", false)
        val lastRecordedTime = sharedPreferences.getLong("last_recorded_time", 0L)
        return Pair(isRunning, lastRecordedTime)
    }

    // Save the last task ID
    fun saveLastTaskId(taskId: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(LAST_TASK_ID_KEY, taskId)
        editor.apply()
    }

    // Load the last task ID
    fun loadLastTaskId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LAST_TASK_ID_KEY, 0) // Default to 0 if no ID is found
    }
}