package com.example.todo_mad.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_mad.R
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.databinding.ActivityAddEditTaskBinding
import com.example.todo_mad.model.Task
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

        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

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
        binding.textViewDueDate.text = taskDueDate

        // Show DatePickerDialog when the due date TextView is clicked
        binding.textViewDueDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSaveTask.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val dueDateText = binding.textViewDueDate.text.toString().trim()

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

            // Validate that the due date is in the future
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dueDate = dateFormat.parse(dueDateText)

            val currentDate = Calendar.getInstance().time
            if (dueDate != null && !dueDate.after(currentDate)) {
                binding.textViewDueDate.error = "Due Date must be a future date"
                return@setOnClickListener
            }

            // If fields are valid and due date is in the future, save or update the task
            if (taskId != null) {
                // Update existing task
                val updatedTask = Task(taskId!!, title, description, dueDateText)
                taskRepository.editTask(updatedTask)
            } else {
                // Add new task
                val newTask = Task(0, title, description, dueDateText)
                taskRepository.addTask(newTask)
            }
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
}
