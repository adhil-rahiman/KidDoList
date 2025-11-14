package com.droidnotes.kiddolist.feature.taskboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@Composable
fun HistoryScreen() {
    // ViewModel injects TaskRepository internally via Koin, with in-memory fallback
    val vm = remember { HistoryViewModel() }
    val completed by vm.completed.collectAsState()
    val scope = rememberCoroutineScope()

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
    ) {
        item(key = "history_title") {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    "Completed Task",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1F1F1F)
                )
                TextButton(
                    onClick = {
                        scope.launch { vm.clearAll(completed.map { it.id }) }
                    },
                    enabled = completed.isNotEmpty()
                ) {
                    androidx.compose.material3.Text(
                        "Clear all",
                        color = if (completed.isNotEmpty()) Color(0xFF2F8CFF) else Color(0xFFB0B6BB)
                    )
                }
            }
        }
        if (completed.isEmpty()) {
            item {
                androidx.compose.material3.Text(
                    "No completed tasks yet",
                    color = Color(0xFF5F6368)
                )
            }
        } else {
            items(completed, key = { it.id }) { task ->
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            androidx.compose.material3.Text(task.title, color = Color(0xFF27AE60))
                            if (task.content.isNotBlank()) {
                                androidx.compose.material3.Text(task.content, color = Color(0xFF5F6368))
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.IconButton(onClick = {
                                scope.launch { vm.restore(task.id) }
                            }) {
                                androidx.compose.material3.Text("↩️")
                            }
                            TextButton(onClick = { scope.launch { vm.delete(task.id) } }) {
                                androidx.compose.material3.Text(
                                    "Clear",
                                    color = Color(0xFFD7263D)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}