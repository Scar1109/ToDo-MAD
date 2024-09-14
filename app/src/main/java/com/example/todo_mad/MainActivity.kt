package com.example.todo_mad.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo_mad.R
import com.example.todo_mad.TaskReminderReceiver
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.databinding.ActivityMainBinding
import com.example.todo_mad.model.Task
import com.example.todo_mad.util.NotificationHelper
import com.example.todo_mad.utill.TaskWidgetProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskListAdapter: TaskListAdapter

    // Register the request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create the notification channel
        NotificationHelper.createNotificationChannel(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskRepository = TaskRepository(this)
        taskListAdapter = TaskListAdapter(taskRepository.getAllTasks(), this::deleteTask, this::editTask)

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = taskListAdapter

        binding.buttonGoToTimer.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }

        // Schedule notifications for all tasks
        scheduleAllTaskNotifications()
    }

    override fun onResume() {
        super.onResume()
        taskListAdapter.updateTasks(taskRepository.getAllTasks())
        TaskWidgetProvider.updateWidget(this) // Trigger widget update when the activity resumes
    }

    private fun deleteTask(taskId: Int) {
        taskRepository.deleteTask(taskId)
        taskListAdapter.updateTasks(taskRepository.getAllTasks())
        TaskWidgetProvider.updateWidget(this) // Trigger widget update after task deletion
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra("taskId", task.id)
            putExtra("taskTitle", task.title)
            putExtra("taskDescription", task.description)
            putExtra("taskDueDate", task.dueDate)
        }
        startActivity(intent)
    }

    private fun scheduleAllTaskNotifications() {
        val tasks = taskRepository.getAllTasks()
        for (task in tasks) {
            scheduleNotification(task)
        }
    }

    private fun scheduleNotification(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Parse the task's due date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val taskDueDate = dateFormat.parse(task.dueDate)

        if (taskDueDate != null) {
            // Schedule the notification for one day before the task's due date
            val calendar = Calendar.getInstance().apply {
                time = taskDueDate
                add(Calendar.DAY_OF_YEAR, -1) // One day before
            }

            val intent = Intent(this, TaskReminderReceiver::class.java).apply {
                putExtra("taskTitle", task.title)
                putExtra("taskDescription", task.description)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                task.id, // Unique ID for each task
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
