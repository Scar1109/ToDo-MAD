package com.example.todo_mad.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_mad.R
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.databinding.ActivityAddEditTaskBinding
import com.example.todo_mad.model.Task

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var taskRepository: TaskRepository
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.addEditTaskLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskRepository = TaskRepository(this)

        // Get task details from the intent if editing
        taskId = intent.getIntExtra("taskId", -1).takeIf { it != -1 }
        val taskTitle = intent.getStringExtra("taskTitle") ?: ""
        val taskDescription = intent.getStringExtra("taskDescription") ?: ""
        val taskDueDate = intent.getStringExtra("taskDueDate") ?: ""

        // Prepopulate fields if editing
        binding.editTextTitle.setText(taskTitle)
        binding.editTextDescription.setText(taskDescription)
        binding.editTextDueDate.setText(taskDueDate)

        binding.buttonSaveTask.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()
            val dueDate = binding.editTextDueDate.text.toString()

            if (taskId != null) {
                // Update existing task
                val updatedTask = Task(taskId!!, title, description, dueDate)
                taskRepository.editTask(updatedTask)
            } else {
                // Add new task
                val newTask = Task(0, title, description, dueDate)
                taskRepository.addTask(newTask)
            }
            finish()
        }
    }
}
