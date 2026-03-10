package com.example.taskflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    flowCoinsBalance: Int,
    purchasedItems: Set<String>,
    onCreateTaskClick: () -> Unit,
    onEditTaskClick: (Task) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onPurgeTasks: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Toutes", "À faire", "En retard", "Validées")

    val priorityWeight = mapOf("Élevée" to 3, "Moyenne" to 2, "Faible" to 1)

    val ownedAnimations = purchasedItems.filter { it in listOf("1", "2", "3", "4") }

    val filteredTasks = tasks.filter { task ->
        when (selectedTabIndex) {
            1 -> !task.isDone
            2 -> !task.isDone && task.dueDate.isNotBlank()
            3 -> task.isDone
            else -> true
        }
    }.sortedByDescending { task ->
        priorityWeight[task.priority] ?: 0
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("My Todo List", color = MaterialTheme.colorScheme.onPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                    actions = {
                        TextButton(onClick = onNavigateToShop) {
                            Text(
                                text = "⭐ $flowCoinsBalance",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }


                    }
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                val count = when(index) {
                                    0 -> tasks.size
                                    1 -> tasks.count { !it.isDone }
                                    2 -> tasks.count { !it.isDone && it.dueDate.isNotBlank() }
                                    3 -> tasks.count { it.isDone }
                                    else -> 0
                                }
                                Text("$title ($count)")
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, "Ajouter", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        if (filteredTasks.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Aucune tâche", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                Text("Appuyez sur + pour créer une nouvelle tâche", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredTasks) { task ->
                    val isLate = !task.isDone && task.dueDate.isNotBlank()
                    var showClassicDialog by remember { mutableStateOf(false) }
                    var activeCelebrationId by remember { mutableStateOf<String?>(null) }

                    if (showClassicDialog) {
                        AlertDialog(
                            onDismissRequest = { showClassicDialog = false },
                            title = { Text("Bien joué !") },
                            text = { Text("Tâche accomplie avec succès.") },
                            confirmButton = { Button(onClick = { showClassicDialog = false }) { Text("Continuer") } }
                        )
                    }

                    if (activeCelebrationId != null) {
                        val (emojis, title, message) = when (activeCelebrationId) {
                            "1" -> Triple("🎉✨🎊", "Félicitations !", "Pluie de confettis activée !")
                            "2" -> Triple("🎆🎇🚀", "BOUM !", "Feu d'artifice impressionnant !")
                            "3" -> Triple("🌟👑💰", "Incroyable !", "Explosion dorée magnifique !")
                            "4" -> Triple("🌠🌌💫", "Magique !", "Pluie d'étoiles filantes !")
                            else -> Triple("✅", "Bravo !", "Tâche terminée.")
                        }

                        Dialog(onDismissRequest = { activeCelebrationId = null }) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(emojis, fontSize = 64.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(message, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(onClick = { activeCelebrationId = null }) {
                                        Text("Super !")
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onEditTaskClick(task) },
                        border = if (isLate) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = task.isDone,
                                onCheckedChange = { isChecked ->
                                    onTaskCheckedChange(task, isChecked)
                                    if (isChecked) {
                                        if(ownedAnimations.isNotEmpty()){
                                            activeCelebrationId = ownedAnimations.random()
                                        } else{
                                            showClassicDialog = true
                                        }
                                    }
                                }
                            )

                            if (task.imageUri != null) {
                                AsyncImage(
                                    model = android.net.Uri.parse(task.imageUri), // <-- LA MODIFICATION EST ICI
                                    contentDescription = "Miniature de la tâche",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(task.title, style = MaterialTheme.typography.titleMedium, textDecoration = if (task.isDone) TextDecoration.LineThrough else null)
                                if (task.description.isNotBlank()) {
                                    Text(task.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                    if (isLate) {
                                        val formattedDate = if (task.dueDate.length == 8) {
                                            "${task.dueDate.substring(0, 2)}/${task.dueDate.substring(2, 4)}/${task.dueDate.substring(4, 8)}"
                                        } else {
                                            task.dueDate
                                        }

                                        Surface(color = Color(0xFFFFEBEE), shape = MaterialTheme.shapes.small) {
                                            Text("⏰ ${formattedDate}", color = Color.Red, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.small) {
                                        Text(task.priority, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (task.periodicity != "Aucune") {
                                        Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.small) {
                                            Text("🔁 ${task.periodicity}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}