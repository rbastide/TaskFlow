package com.example.taskflow

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
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

class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += ":"
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 1) offset else if (offset <= 4) offset + 1 else 5
            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 2) offset else if (offset <= 5) offset - 1 else 4
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", externalCacheDir)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(onTaskCreated: (Task) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }

    var selectedImageUri by remember {mutableStateOf<String?>(null)}
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    val priorityOptions = listOf("Faible", "Moyenne", "Élevée")
    var expandedPriority by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(priorityOptions[1]) }

    val periodicityOptions = listOf("Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle", "Annuelle")
    var expandedPeriodicity by remember { mutableStateOf(false) }
    var periodicity by remember { mutableStateOf(periodicityOptions[0]) }
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
                title = { Text("Nouvelle tâche", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer la page",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dueDate, onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) dueDate = it }, label = { Text("Date (JJMMAAAA)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), visualTransformation = DateVisualTransformation()
                )
                OutlinedTextField(
                    value = dueTime,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) dueTime = it
                    },
                    label = { Text("Heure (HHMM)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = TimeVisualTransformation()
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
                Text("✅ Image attachée avec succès", color = MaterialTheme.colorScheme.primary)
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

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskCreated(
                            Task(
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                dueTime = dueTime,
                                priority = priority,
                                periodicity = periodicity,
                                imageUri = selectedImageUri
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()
            ) { Text("Enregistrer") }
        }
    }
}