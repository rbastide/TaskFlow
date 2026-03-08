package com.example.taskflow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val tasks = remember { mutableStateListOf<Task>() }

    NavHost(navController = navController, startDestination = "taskList", modifier = modifier) {

        composable("taskList") {
            TaskListScreen(
                tasks = tasks,
                onCreateTaskClick = { navController.navigate("createTask") },
                onEditTaskClick = { task -> navController.navigate("editTask/${task.id}") },
                onTaskCheckedChange = { task, isChecked ->
                    val index = tasks.indexOfFirst { it.id == task.id }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(isDone = isChecked)
                    }
                },
                onPurgeTasks = {
                    tasks.removeAll { it.isDone }
                }
            )
        }

        composable("createTask") {
            CreateTaskScreen(
                onTaskCreated = { newTask ->
                    tasks.add(newTask)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("editTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            val taskToEdit = tasks.find { it.id == taskId }

            if (taskToEdit != null) {
                EditTaskScreen(
                    task = taskToEdit,
                    onTaskUpdated = { newTitle, newDescription ->
                        val index = tasks.indexOfFirst { it.id == taskId }
                        if (index != -1) {
                            tasks[index] = tasks[index].copy(title = newTitle, description = newDescription)
                        }
                        navController.popBackStack()
                    },
                    onDeleteTask = {
                        tasks.removeAll { it.id == taskId }
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}