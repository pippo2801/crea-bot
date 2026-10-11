package com.pippo2801.universalgamebot.ui

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.pippo2801.universalgamebot.service.BotForegroundService
import com.pippo2801.universalgamebot.ui.screens.*
import com.pippo2801.universalgamebot.ui.theme.UniversalGameBotTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val screenCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val metrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getRealMetrics(metrics)
                try {
                    val serviceIntent = Intent(this, BotForegroundService::class.java)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                    viewModel.startBot(
                        result.resultCode, result.data!!,
                        metrics.widthPixels, metrics.heightPixels, metrics.densityDpi
                    )
                } catch (e: Exception) {
                    viewModel.botLoop.reportStatus("Avvio non riuscito: ${e.message ?: "errore"}")
                    stopService(Intent(this, BotForegroundService::class.java))
                }
            } else {
                viewModel.botLoop.reportStatus("Permesso di acquisizione schermo negato; bot fermo")
            }
        }

    private fun requestScreenCapture() {
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureLauncher.launch(manager.createScreenCaptureIntent())
    }

    private fun stopObservation() {
        viewModel.stopBot()
        stopService(Intent(this, BotForegroundService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniversalGameBotTheme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Home, Screen.Games, Screen.Learning, Screen.Completed,
                    Screen.Laboratory, Screen.AiResources, Screen.Settings
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val entry by navController.currentBackStackEntryAsState()
                            val currentRoute = entry?.destination?.route
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
                                            },
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                        composable(Screen.Home.route) {
                            HomeScreen(viewModel, onStart = ::requestScreenCapture, onStop = ::stopObservation)
                        }
                        composable(Screen.Games.route) {
                            GamesScreen(viewModel) { gameId -> navController.navigate("game_details/$gameId") }
                        }
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
