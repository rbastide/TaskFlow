package com.example.taskflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onCreateTaskClick: () -> Unit,
    onEditTaskClick: (Task) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Todo List") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditTaskClick(task) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { isChecked -> onTaskCheckedChange(task, isChecked) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                            )
                            if (task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}