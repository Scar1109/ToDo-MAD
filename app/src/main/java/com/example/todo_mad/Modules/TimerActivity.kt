package com.example.todo_mad.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_mad.R
import com.example.todo_mad.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private var isRunning = false
    private var isPaused = false
    private var timeInMillis: Long = 0L
    private var startTime: Long = 0L
    private var pauseOffset: Long = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                // Calculate the elapsed time
                val elapsedMillis = System.currentTimeMillis() - startTime
                timeInMillis = elapsedMillis + pauseOffset

                // Update the timer display
                updateTimerTextView()

                // Re-post the runnable to continue updating the time
                handler.postDelayed(this, 10)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

        ViewCompat.setOnApplyWindowInsetsListener(binding.timerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Start button click listener
        binding.buttonStart.setOnClickListener {
            startStopwatch()
        }

        // Pause button click listener
        binding.buttonPause.setOnClickListener {
            pauseStopwatch()
        }

        // Stop button click listener
        binding.buttonStop.setOnClickListener {
            stopStopwatch()
        }

        // Reset button click listener
        binding.buttonReset.setOnClickListener {
            resetStopwatch()
        }
    }

    // Function to start the stopwatch
    private fun startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            handler.post(runnable)
            isRunning = true
            isPaused = false
        }
    }

    // Function to pause the stopwatch
    private fun pauseStopwatch() {
        if (isRunning && !isPaused) {
            handler.removeCallbacks(runnable)
            pauseOffset = timeInMillis
            isPaused = true
            isRunning = false
        }
    }

    // Function to stop the stopwatch
    private fun stopStopwatch() {
        if (isRunning) {
            handler.removeCallbacks(runnable)
            isRunning = false
            isPaused = false
        }
    }

    // Function to reset the stopwatch
    private fun resetStopwatch() {
        handler.removeCallbacks(runnable)
        isRunning = false
        isPaused = false
        timeInMillis = 0L
        pauseOffset = 0L
        binding.textViewTimer.text = "00:00:00:000"
    }

    // Function to update the timer TextView with the elapsed time
    private fun updateTimerTextView() {
        val hours = (timeInMillis / 3600000).toInt()
        val minutes = ((timeInMillis % 3600000) / 60000).toInt()
        val seconds = ((timeInMillis % 60000) / 1000).toInt()
        val milliseconds = (timeInMillis % 1000).toInt()

        binding.textViewTimer.text = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)
    }
}
