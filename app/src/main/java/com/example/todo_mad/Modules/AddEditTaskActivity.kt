package com.example.todo_mad.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_mad.R
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.databinding.ActivityAddEditTaskBinding
import com.example.todo_mad.model.Task
import com.example.todo_mad.util.TaskReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var taskRepository: TaskRepository
    private var taskId: Int? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

        ViewCompat.setOnApplyWindowInsetsListener(binding.addEditTaskLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskRepository = TaskRepository(this)

        // Check if the app can schedule exact alarms
        checkExactAlarmPermission()

        // Get task details from the intent if editing
        taskId = intent.getIntExtra("taskId", -1).takeIf { it != -1 }
        val taskTitle = intent.getStringExtra("taskTitle") ?: ""
        val taskDescription = intent.getStringExtra("taskDescription") ?: ""
        val taskDueDate = intent.getStringExtra("taskDueDate") ?: ""
        val taskDueTime = intent.getStringExtra("taskDueTime") ?: ""

        // Prepopulate fields if editing
        binding.editTextTitle.setText(taskTitle)
        binding.editTextDescription.setText(taskDescription)
        binding.textViewDueDate.text = taskDueDate
        binding.textViewDueTime.text = taskDueTime

        // Show DatePickerDialog when the due date TextView is clicked
        binding.textViewDueDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Show TimePickerDialog when the due time TextView is clicked
        binding.textViewDueTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.buttonSaveTask.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val dueDateText = binding.textViewDueDate.text.toString().trim()
            val dueTimeText = binding.textViewDueTime.text.toString().trim()

            // Validate fields
            if (title.isEmpty()) {
                binding.editTextTitle.error = "Title is required"
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                binding.editTextDescription.error = "Description is required"
                return@setOnClickListener
            }
            if (dueDateText.isEmpty()) {
                binding.textViewDueDate.error = "Due Date is required"
                return@setOnClickListener
            }
            if (dueTimeText.isEmpty()) {
                binding.textViewDueTime.error = "Due Time is required"
                return@setOnClickListener
            }

            // Validate that the due date is today or in the future
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dueDate = dateFormat.parse(dueDateText)
            val currentDate = Calendar.getInstance().time

            if (dueDate != null && dueDate.before(dateFormat.parse(dateFormat.format(currentDate)))) {
                binding.textViewDueDate.error = "Due Date must not be in the past"
                return@setOnClickListener
            }

            // If fields are valid, save or update the task
            val newTask = if (taskId != null) {
                // Update existing task
                Task(taskId!!, title, description, dueDateText, dueTimeText)
            } else {
                // Add new task
                Task(0, title, description, dueDateText, dueTimeText)
            }

            if (taskId != null) {
                taskRepository.editTask(newTask)
            } else {
                taskRepository.addTask(newTask)
            }

            scheduleNotification(newTask) // Schedule notification for the task
            finish()
        }
    }

    // Function to show the DatePickerDialog
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDueDateTextView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Function to update the due date TextView with the selected date
    private fun updateDueDateTextView() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.textViewDueDate.text = dateFormat.format(calendar.time)
    }

    // Function to show the TimePickerDialog
    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDueTimeTextView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    // Function to update the due time TextView with the selected time
    private fun updateDueTimeTextView() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.textViewDueTime.text = timeFormat.format(calendar.time)
    }

    // Check if the app can schedule exact alarms
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31 (Android 12)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
            }
        }
    }

    // Request permission to schedule exact alarms
    private fun requestExactAlarmPermission() {
        AlertDialog.Builder(this)
            .setTitle("Allow Exact Alarms")
            .setMessage("To remind you at the right time, the app needs permission to set exact alarms. Please allow this permission in the settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Schedule notification
    private fun scheduleNotification(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TaskReminderReceiver::class.java)
        intent.putExtra("taskTitle", task.title)
        intent.putExtra("taskDescription", task.description)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate taskDateTime in milliseconds
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val taskDateTime = dateFormat.parse("${task.dueDate} ${task.dueTime}")?.time

        try {
            // Check if exact alarm permission is granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31 (Android 12)
                if (alarmManager.canScheduleExactAlarms()) {
                    taskDateTime?.let {
                        scheduleExactAlarm(alarmManager, it, pendingIntent)
                    }
                } else {
                    checkExactAlarmPermission()
                }
            } else {
                // For devices with API level < 31, no permission check is needed
                taskDateTime?.let {
                    scheduleExactAlarm(alarmManager, it, pendingIntent)
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Cannot schedule exact alarm. Please allow the permission in settings.", Toast.LENGTH_LONG).show()
            checkExactAlarmPermission()
        }
    }

    // Helper function to schedule exact alarm
    private fun scheduleExactAlarm(alarmManager: AlarmManager, taskDateTime: Long, pendingIntent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, taskDateTime, pendingIntent)
    }
}
