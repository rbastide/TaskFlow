package com.example.taskflow

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onDeleteTask: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var dueTime by remember { mutableStateOf(task.dueTime) }

    val priorityOptions = listOf("Faible", "Moyenne", "Élevée")
    var expandedPriority by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(task.priority) }

    val periodicityOptions = listOf("Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle", "Annuelle")
    var expandedPeriodicity by remember { mutableStateOf(false) }
    var periodicity by remember { mutableStateOf(task.periodicity) }

    var selectedImageUri by remember { mutableStateOf<String?>(task.imageUri) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    val isDateValid = isValidDate(dueDate)
    val isTimeValid = isValidTime(dueTime)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
                selectedImageUri = uri.toString()
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && currentPhotoPath != null) {
                selectedImageUri = Uri.fromFile(File(currentPhotoPath!!)).toString()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier la tâche") },
                actions = {
                    IconButton(onClick = onDeleteTask) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer la tâche",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Titre de la tâche *") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) dueDate = it },
                    label = { Text("Date (JJMMAAAA)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DateVisualTransformation(),
                    isError = dueDate.isNotEmpty() && !isDateValid
                )
                OutlinedTextField(
                    value = dueTime,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) dueTime = it },
                    label = { Text("Heure (HHMM)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = TimeVisualTransformation(),
                    isError = dueTime.isNotEmpty() && !isTimeValid
                )
            }

            ExposedDropdownMenuBox(expanded = expandedPriority, onExpandedChange = { expandedPriority = !expandedPriority }) {
                OutlinedTextField(
                    value = priority, onValueChange = {}, readOnly = true, label = { Text("Priorité") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedPriority, onDismissRequest = { expandedPriority = false }) {
                    priorityOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { priority = option; expandedPriority = false })
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = expandedPeriodicity, onExpandedChange = { expandedPeriodicity = !expandedPeriodicity }) {
                OutlinedTextField(
                    value = periodicity, onValueChange = {}, readOnly = true, label = { Text("Périodicité") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriodicity) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedPeriodicity, onDismissRequest = { expandedPeriodicity = false }) {
                    periodicityOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { periodicity = option; expandedPeriodicity = false })
                    }
                }
            }

            if (selectedImageUri != null) {
                Text("✅ Image attachée : ${if (selectedImageUri == task.imageUri) "conservée" else "modifiée"}", color = MaterialTheme.colorScheme.primary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📁 Galerie")
                }

                OutlinedButton(
                    onClick = {
                        val file = context.createImageFile()
                        currentPhotoPath = file.absolutePath
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Caméra")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Annuler") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank() && isDateValid && isTimeValid) {
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                priority = priority,
                                dueDate = dueDate,
                                dueTime = dueTime,
                                periodicity = periodicity,
                                imageUri = selectedImageUri
                            )
                            onTaskUpdated(updatedTask)
                        }
                    },
                    enabled = title.isNotBlank() && isDateValid && isTimeValid
                ) { Text("Sauvegarder") }
            }
        }
    }
}