package com.example.todo_mad.utill

import android.content.Context

object SharedPreferencesHelper {

    private const val PREFS_NAME = "app_prefs"

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
}