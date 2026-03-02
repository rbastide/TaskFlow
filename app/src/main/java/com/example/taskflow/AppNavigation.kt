package com.example.taskflow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // On initialise une liste vide de type Task
    val tasks = remember { mutableStateListOf<Task>() }

    NavHost(navController = navController, startDestination = "taskList", modifier = modifier) {
        composable("taskList") {
            TaskListScreen(
                tasks = tasks,
                onCreateTaskClick = { navController.navigate("createTask") }
            )
        }
        composable("createTask") {
            CreateTaskScreen(
                onTaskCreated = { newTask ->
                    tasks.add(newTask)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}