package com.example.todo_mad.utill

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todo_mad.R
import com.example.todo_mad.data.TaskRepository
import com.example.todo_mad.ui.MainActivity

class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // Helper function to update the widget
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_task)

        // Retrieve the most recent task from internal storage
        val taskRepository = TaskRepository(context)
        val tasks = taskRepository.getAllTasks()

        // Get the most recent task
        val mostRecentTask = tasks.maxByOrNull { task -> task.dueDate } // Adjust sorting criteria as needed

        // Display the most recent task or a default message if no tasks are available
        if (mostRecentTask != null) {
            views.setTextViewText(R.id.widgetTaskList, "${mostRecentTask.title}: ${mostRecentTask.dueDate}")
        } else {
            views.setTextViewText(R.id.widgetTaskList, "No upcoming tasks")
        }

        // Intent to open the app when clicking on the widget
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Specify FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetTaskList, pendingIntent)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        // Static method to manually trigger widget updates
        fun updateWidget(context: Context) {
            val intent = Intent(context, TaskWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, TaskWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
