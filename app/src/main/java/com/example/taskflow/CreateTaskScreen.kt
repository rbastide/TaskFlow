package com.example.taskflow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) { out += trimmed[i]; if (i == 1 || i == 3) out += "/" }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = if (offset <= 1) offset else if (offset <= 3) offset + 1 else if (offset <= 8) offset + 2 else 10
            override fun transformedToOriginal(offset: Int): Int = if (offset <= 2) offset else if (offset <= 5) offset - 1 else if (offset <= 10) offset - 2 else 8
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(onTaskCreated: (Task) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }

    val priorityOptions = listOf("Faible", "Moyenne", "Élevée")
    var expandedPriority by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(priorityOptions[1]) }

    val periodicityOptions = listOf("Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle", "Annuelle")
    var expandedPeriodicity by remember { mutableStateOf(false) }
    var periodicity by remember { mutableStateOf(periodicityOptions[0]) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nouvelle tâche", color = MaterialTheme.colorScheme.onPrimary) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dueDate, onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) dueDate = it }, label = { Text("Date (JJMMAAAA)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), visualTransformation = DateVisualTransformation()
                )
                OutlinedTextField(
                    value = dueTime, onValueChange = { if (it.all { char -> char.isDigit() }) dueTime = it }, label = { Text("Heure") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text("📷 Joindre une image")
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { if (title.isNotBlank()) onTaskCreated(Task(title = title, description = description, dueDate = dueDate, dueTime = dueTime, priority = priority, periodicity = periodicity)) },
                modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()
            ) { Text("Enregistrer") }
        }
    }
}