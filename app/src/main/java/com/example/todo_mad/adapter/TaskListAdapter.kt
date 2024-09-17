package com.example.todo_mad.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_mad.R
import com.example.todo_mad.model.Task

class TaskListAdapter(
    private var tasks: List<Task>,
    private val onDeleteTask: (Int) -> Unit,   // Callback to handle task deletion
    private val onEditTask: (Task) -> Unit     // Callback to handle task editing
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewDueDate: TextView = itemView.findViewById(R.id.textViewDueDate)
        private val buttonDelete: ImageView = itemView.findViewById(R.id.buttonDelete)
        private val buttonEdit: ImageView = itemView.findViewById(R.id.buttonEdit)

        fun bind(task: Task) {
            textViewTitle.text = task.title
            textViewDescription.text = task.description
            textViewDueDate.text = task.dueDate

            // Handle delete button click to delete the specific task
            buttonDelete.setOnClickListener {
                onDeleteTask(task.id) // Pass the task ID to the callback for deletion
            }

            // Handle edit button click to edit the specific task
            buttonEdit.setOnClickListener {
                onEditTask(task) // Pass the task object to the callback for editing
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
