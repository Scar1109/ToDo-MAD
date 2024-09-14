package com.example.todo_mad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo_mad.util.NotificationHelper

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve task details from intent
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Task"
        val taskDescription = intent.getStringExtra("taskDescription") ?: "You have an upcoming task!"

        // Show the notification
        NotificationHelper.showNotification(context, taskTitle, taskDescription)
    }
}
