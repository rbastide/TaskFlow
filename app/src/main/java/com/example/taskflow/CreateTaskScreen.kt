package com.example.taskflow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(onTaskCreated: (Task) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Moyenne") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle tâche", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Date d'échéance") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = dueTime, onValueChange = { dueTime = it }, label = { Text("Heure") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priorité") }, modifier = Modifier.fillMaxWidth())

            OutlinedButton(onClick = { /* TODO Image */ }, modifier = Modifier.fillMaxWidth()) {
                Text("📷 Joindre une image")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskCreated(Task(title = title, description = description, dueDate = dueDate, dueTime = dueTime, priority = priority))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Enregistrer")
            }
        }
    }
}