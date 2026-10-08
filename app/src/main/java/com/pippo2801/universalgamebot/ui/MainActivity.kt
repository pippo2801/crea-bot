package com.pippo2801.universalgamebot.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.pippo2801.universalgamebot.ui.screens.*
import com.pippo2801.universalgamebot.ui.theme.UniversalGameBotTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniversalGameBotTheme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Home,
                    Screen.Games,
                    Screen.Learning,
                    Screen.Completed,
                    Screen.Laboratory,
                    Screen.AiResources,
                    Screen.Settings
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            when (screen) {
                                                Screen.Home -> Icons.Default.Home
                                                Screen.Games -> Icons.Default.SportsEsports
                                                Screen.Learning -> Icons.Default.School
                                                Screen.Completed -> Icons.Default.CheckCircle
                                                Screen.Laboratory -> Icons.Default.Science
                                                Screen.AiResources -> Icons.Default.SmartToy
                                                Screen.Settings -> Icons.Default.Settings
                                                else -> Icons.Default.Home
                                            },
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) { HomeScreen(viewModel) }
                        composable(Screen.Games.route) { GamesScreen(viewModel) { gameId -> navController.navigate("game_details/$gameId") } }
                        composable(Screen.Learning.route) { LearningScreen(viewModel) }
                        composable(Screen.Completed.route) { CompletedScreen(viewModel) }
                        composable(Screen.Laboratory.route) { LaboratoryScreen(viewModel) }
                        composable(Screen.AiResources.route) { AiResourcesScreen(viewModel) }
                        composable(Screen.Settings.route) { SettingsScreen(viewModel) }
                        composable(
                            route = "game_details/{gameId}",
                            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                            GameDetailsScreen(viewModel, gameId)
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Games : Screen("games", "Games")
    object Learning : Screen("learning", "Learn")
    object Completed : Screen("completed", "Completed")
    object Laboratory : Screen("laboratory", "Lab")
    object AiResources : Screen("ai_resources", "AI")
    object Settings : Screen("settings", "Settings")
}
