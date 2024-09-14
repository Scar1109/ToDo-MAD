package com.example.todo_mad.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_mad.R
import com.example.todo_mad.databinding.ActivityTimerBinding
import com.example.todo_mad.utill.SharedPreferencesHelper

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private var isRunning = false
    private var lastRecordedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.timerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val (savedIsRunning, savedLastTime) = SharedPreferencesHelper.loadTimerSettings(this)
        isRunning = savedIsRunning
        lastRecordedTime = savedLastTime

        binding.buttonStartStop.setOnClickListener {
            isRunning = !isRunning
            updateTimerUI()
            SharedPreferencesHelper.saveTimerSettings(isRunning, lastRecordedTime, this)
        }

        binding.buttonReset.setOnClickListener {
            isRunning = false
            lastRecordedTime = 0L
            updateTimerUI()
            SharedPreferencesHelper.saveTimerSettings(isRunning, lastRecordedTime, this)
        }
    }

    private fun updateTimerUI() {
        binding.textViewTimer.text = lastRecordedTime.toString()
        binding.buttonStartStop.text = if (isRunning) "Stop" else "Start"
    }
}
