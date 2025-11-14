package com.droidnotes.kiddolist.feature.taskboard

import InMemoryTaskRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.listOf
import kotlin.text.isNotBlank
import kotlin.to

@Composable
fun TaskBoardScreen(
    onAdd: () -> Unit = {},
    onEdit: (Long) -> Unit = {},
) {
    // ViewModel injects TaskRepository internally via Koin, with in-memory fallback
    val vm = remember { TaskBoardViewModel() }
    val tasks by vm.tasks.collectAsState()
    val listState = rememberLazyListState()
    var previousSize by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Subtle one-item scroll when a new task arrives and user isn't at top
    LaunchedEffect(tasks.size) {
        val newSize = tasks.size
        val atTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        if (previousSize > 0 && newSize > previousSize && !atTop) {
            val targetIndex = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
        previousSize = newSize
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            // small margin around the content per new design
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        state = listState
    ) {
        // Small top toolbar with Sync (separate from the Add Task header)
        item(key = "toolbar_sync") {
            var isSyncing by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (!isSyncing) {
                            isSyncing = true
                            scope.launch {
                                // Simulate network delay
                                delay(1000)
                                val dummy = listOf(
                                    "Brushing" to "Brush teeth for 2 minutes 🪥",
                                    "Studying" to "Read a short story or learn ABC 📚",
                                    "Play" to "Outdoor playtime or puzzles 🧩",
                                    "Good night" to "Brush teeth, pajamas on, lights out 🌙",
                                )
                                // Avoid duplicates by title
                                val existingTitles = tasks.map { it.title }.toSet()
                                dummy.filter { it.first !in existingTitles }
                                    .forEach { (title, content) -> vm.addTask(title, content) }
                                isSyncing = false
                            }
                        }
                    },
                    enabled = !isSyncing,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F8CFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isSyncing) "Syncing…" else "Sync 🔄", fontWeight = FontWeight.Bold)
                }
            }
        }
        // Header card
        item(key = "header") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F8CFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tasks",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAdd,
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF2F8CFF))
                        ) { Text("Add ✨", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }

        // Quick add label
        item(key = "quick_add_label") {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Quick add", fontWeight = FontWeight.SemiBold)
            }
        }

        // Quick add row
        item(key = "quick_add_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PredefinedTask(
                    emoji = "🪥",
                    label = "Brushing",
                    bg = Color(0xFFE8F1FF),
                    onClick = { vm.addTask("Brushing", "Brush teeth for 2 minutes 🪥") }
                )
                PredefinedTask(
                    emoji = "⏰",
                    label = "Study",
                    bg = Color(0xFFE7FFF3),
                    onClick = { vm.addTask("Studying", "Read a short story or learn ABC 📚") }
                )
                PredefinedTask(
                    emoji = "🌙",
                    label = "Good night",
                    bg = Color(0xFFFFF0F3),
                    onClick = { vm.addTask("Good night", "Brush teeth, pajamas on, lights out 🌙") }
                )
                PredefinedTask(
                    emoji = "🎈",
                    label = "Play",
                    bg = Color(0xFFFFF7E6),
                    onClick = { vm.addTask("Play", "Outdoor playtime or puzzles 🧩") }
                )
            }
        }

        // Divider
        item(key = "divider") { Divider(color = Color(0x11000000)) }

        // Task items
        items(tasks, key = { it.id }) { task ->
            TaskRow(
                task = task,
                onToggle = { vm.toggleDone(task.id) },
                onDelete = { vm.delete(task.id) },
                onEdit = { onEdit(task.id) }
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isDone, onCheckedChange = { onToggle() })
                Column(Modifier.padding(start = 8.dp)) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (task.isDone) Color(0xFF27AE60) else Color(0xFF2F8CFF)
                    )
                    if (task.content.isNotBlank()) {
                        Text(task.content, style = MaterialTheme.typography.bodySmall, color = Color(0xFF5F6368))
                    }
                }
            }
            IconButton(onClick = onDelete) {
                // Emoji-based icon to avoid extra icon dependencies
                Text("🗑️")
            }
        }
    }
}

@Composable
private fun PredefinedTask(emoji: String, label: String, bg: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .background(bg, shape = RoundedCornerShape(50))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text(emoji, textAlign = TextAlign.Center) }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = Color(0xFF5F6368))
    }
}
