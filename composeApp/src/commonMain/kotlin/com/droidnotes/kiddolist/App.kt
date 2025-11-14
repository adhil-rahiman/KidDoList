package com.droidnotes.kiddolist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.droidnotes.kiddolist.feature.taskboard.AddEditTaskScreen
import com.droidnotes.kiddolist.feature.taskboard.HistoryScreen
import com.droidnotes.kiddolist.feature.taskboard.TaskBoardScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.collections.listOf

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val route = backStackEntry?.destination?.route
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F)
                ) {
                    NavigationBarItem(
                        selected = route == "tasks",
                        onClick = { navController.navigate("tasks") },
                        icon = { androidx.compose.material3.Text("🏠") },
                        label = { androidx.compose.material3.Text("Task") }
                    )
                    NavigationBarItem(
                        selected = route == "add",
                        onClick = { navController.navigate("add") },
                        icon = { androidx.compose.material3.Text("➕") },
                        label = { androidx.compose.material3.Text("Add Task") }
                    )
                    NavigationBarItem(
                        selected = route == "history",
                        onClick = { navController.navigate("history") },
                        icon = { androidx.compose.material3.Text("🕘") },
                        label = { androidx.compose.material3.Text("History") }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NavHost(navController = navController, startDestination = "tasks") {
                    composable("tasks") {
                        TaskBoardScreen(
                            onAdd = { navController.navigate("add") },
                            onEdit = { id -> navController.navigate("edit/$id") }
                        )
                    }
                    composable("add") {
                        AddEditTaskScreen(
                            taskId = null,
                            onDone = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "edit/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("taskId")
                        AddEditTaskScreen(
                            taskId = id,
                            onDone = { navController.popBackStack() }
                        )
                    }
                    composable("history") {
                        HistoryScreen()
                    }
                }
            }
        }
    }
}