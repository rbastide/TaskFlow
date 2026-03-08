package com.example.taskflow

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,
    onTaskUpdated: (String, String) -> Unit,
    onDeleteTask: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier la tâche") },
                actions = {
                    IconButton(onClick = onDeleteTask) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                            contentDescription = "Supprimer la tâche",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Titre de la tâche") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optionnelle)") }, modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Annuler") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (title.isNotBlank()) onTaskUpdated(title, description) },
                    enabled = title.isNotBlank()
                ) { Text("Sauvegarder") }
            }
        }
    }
}