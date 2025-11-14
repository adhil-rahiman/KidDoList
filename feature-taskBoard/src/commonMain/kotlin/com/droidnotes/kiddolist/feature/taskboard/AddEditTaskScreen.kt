package com.droidnotes.kiddolist.feature.taskboard

import InMemoryTaskRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
// import androidx.compose.material3.TextFieldDefaults // not used
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.launch
import kotlin.text.buildString

@Composable
fun AddEditTaskScreen(
    repository: TaskRepository? = null,
    taskId: Long? = null,
    onDone: () -> Unit = {},
) {
    val repo = remember(repository) { repository ?: InMemoryTaskRepository() }
    val scope = rememberCoroutineScope()
    var loadedTask by remember { mutableStateOf<Task?>(null) }
    var noteText by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = repo.get(taskId)
            loadedTask = task
            val combined = buildString {
                append(task?.title.orEmpty())
                val body = task?.content.orEmpty()
                if (body.isNotBlank()) {
                    append('\n')
                    append(body)
                }
            }
            noteText = combined
        } else {
            loadedTask = null
            noteText = ""
        }
    }

    val bgColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onDone) { Text("Cancel") }
            Text(if (taskId == null) "New Task" else "Edit Task", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
            val canSave = noteText.lineSequence().firstOrNull()?.isNotBlank() == true
            androidx.compose.material3.Button(
                onClick = {
                scope.launch {
                    val lines = noteText.lines()
                    val title = lines.firstOrNull()?.trim().orEmpty()
                    val content = lines.drop(1).joinToString("\n").trimEnd()
                    if (loadedTask == null) {
                        repo.add(title, content)
                    } else {
                        repo.update(loadedTask!!.id, title, content)
                    }
                    onDone()
                }
            }, enabled = canSave,
                shape = RoundedCornerShape(28.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F8CFF),
                    contentColor = Color.White
                )
            ) { Text("Save") }
        }

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
            ) {
                if (noteText.isEmpty()) {
                    Column {
                        Text("Title", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold), color = Color(0xFF9AA0A6))
                        Text("\nWrite details here...", style = TextStyle(fontSize = 16.sp), color = Color(0xFFB0B6BB))
                    }
                }
                BasicTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(color = Color(0xFF1F1F1F), fontSize = 18.sp),
                    decorationBox = { inner ->
                        inner()
                    }
                )
            }
        }
    }
}
