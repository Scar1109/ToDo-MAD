package com.example.todo_mad.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.todo_mad.model.Task
import java.io.File
import java.io.InputStreamReader

object LocalStorageHelper {

    // Save the list of tasks to a file
    fun saveTasksToFile(taskList: List<Task>, context: Context) {
        val gson = Gson()
        val jsonString = gson.toJson(taskList)

        // Write the JSON string to a file
        context.openFileOutput("tasks_file.json", Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    // Load the list of tasks from a file
    fun loadTasksFromFile(context: Context): List<Task> {
        val file = File(context.filesDir, "tasks_file.json")

        // Check if the file exists before attempting to open it
        if (!file.exists()) {
            // If the file does not exist, return an empty list
            return emptyList()
        }

        // If the file exists, read its contents
        val gson = Gson()
        val fileInput = context.openFileInput("tasks_file.json")
        val inputStreamReader = InputStreamReader(fileInput)
        val jsonString = inputStreamReader.readText()
        inputStreamReader.close()

        return if (jsonString.isNotEmpty()) {
            val taskType = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(jsonString, taskType)
        } else {
            emptyList()
        }
    }
}
