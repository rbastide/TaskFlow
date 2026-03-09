package com.example.taskflow
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: String = "Moyenne",
    val dueDate: String = "",
    val dueTime: String = "",
    val periodicity: String = "Aucune",
    val imageUri: String? = null
)