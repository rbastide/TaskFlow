package com.example.taskflow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }

    val tasks = remember { mutableStateListOf<Task>().apply { addAll(dataManager.getTasks())}}
    var flowCoins by remember { mutableStateOf(dataManager.getFlowCoins()) }

    var purchasedItems by remember { mutableStateOf(dataManager.getPurchasedItems()) }

    NavHost(navController = navController, startDestination = "taskList", modifier = modifier) {

        composable("taskList") {
            TaskListScreen(
                tasks = tasks,
                flowCoinsBalance = flowCoins,
                purchasedItems = purchasedItems,
                onCreateTaskClick = { navController.navigate("createTask") },
                onEditTaskClick = { task -> navController.navigate("editTask/${task.id}") },
                onTaskCheckedChange = { task, isChecked ->
                    val index = tasks.indexOfFirst { it.id == task.id }
                    if (index != -1) {
                        tasks[index] = tasks[index].copy(isDone = isChecked)
                        dataManager.saveTasks(tasks)
                        if (isChecked) {
                            flowCoins += 10
                            dataManager.saveFlowCoins(flowCoins)
                        }
                    }
                },
                onPurgeTasks = {
                    tasks.removeAll { it.isDone }
                    dataManager.saveTasks(tasks)
                },
                onNavigateToShop = { navController.navigate("shop")}
            )
        }

        composable("createTask") {
            CreateTaskScreen(
                onTaskCreated = { newTask ->
                    tasks.add(newTask)
                    dataManager.saveTasks(tasks)
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
                            dataManager.saveTasks(tasks)
                        }
                        navController.popBackStack()
                    },
                    onDeleteTask = {
                        tasks.removeAll { it.id == taskId }
                        dataManager.saveTasks(tasks)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }

        composable("shop"){
            ShopScreen(
                flowCoinsBalance = flowCoins,
                purchasedItems = purchasedItems,
                onBack = {navController.popBackStack()},
                onBuyItem = {item ->
                    if (flowCoins >= item.price){
                        flowCoins -= item.price
                        dataManager.saveFlowCoins(flowCoins)

                        dataManager.savePurchasedItem(item.id.toString())
                        purchasedItems = dataManager.getPurchasedItems()

                    }
                }
            )
        }
    }
}