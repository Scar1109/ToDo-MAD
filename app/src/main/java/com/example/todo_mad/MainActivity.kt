package com.example.todo_mad.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.databinding.ActivityMainBinding
import com.example.todo_mad.model.Task

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    override fun onResume() {
        super.onResume()
        taskListAdapter.updateTasks(taskRepository.getAllTasks())
    }

    // Function to delete a specific task by its ID
    private fun deleteTask(taskId: Int) {
        taskRepository.deleteTask(taskId)
        taskListAdapter.updateTasks(taskRepository.getAllTasks())
    }

    // Function to edit a task
    private fun editTask(task: Task) {
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra("taskId", task.id)
            putExtra("taskTitle", task.title)
            putExtra("taskDescription", task.description)
            putExtra("taskDueDate", task.dueDate)
        }
        startActivity(intent)
    }
}
