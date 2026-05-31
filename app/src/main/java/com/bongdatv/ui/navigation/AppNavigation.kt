package com.bongdatv.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bongdatv.BuildConfig
import com.bongdatv.ui.home.HomeScreen
import com.bongdatv.ui.player.PlayerScreen
import com.bongdatv.ui.schedule.ScheduleScreen
import com.bongdatv.update.UpdateChecker
import com.bongdatv.update.UpdateDialog
import com.bongdatv.update.UpdateInfo

private const val GITHUB_OWNER = "tronghuy5555"
private const val GITHUB_REPO = "bongdatv"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val dismissTime = prefs.getLong(UpdateChecker.PREF_KEY_DISMISS_TIME, 0)
        if (System.currentTimeMillis() - dismissTime > UpdateChecker.DISMISS_DURATION_MS) {
            val checker = UpdateChecker(okhttp3.OkHttpClient())
            val info = checker.checkForUpdate(BuildConfig.VERSION_NAME, GITHUB_OWNER, GITHUB_REPO)
            if (info != null) {
                updateInfo = info
                showUpdateDialog = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onMatchClick = { fixtureId, streamUrl ->
                        navController.navigate("player/$fixtureId?url=$streamUrl")
                    },
                    onNavigateToSchedule = {
                        navController.navigate("schedule")
                    },
                    updateInfo = updateInfo,
                    onUpdateClick = {
                        updateInfo?.let {
                            val checker = UpdateChecker(okhttp3.OkHttpClient())
                            checker.downloadAndInstall(context, it)
                        }
                    }
                )
            }
            composable("schedule") {
                ScheduleScreen(
                    onMatchClick = { fixtureId, streamUrl ->
                        navController.navigate("player/$fixtureId?url=$streamUrl")
                    }
                )
            }
            composable("player/{fixtureId}?url={streamUrl}") { backStackEntry ->
                val streamUrl = backStackEntry.arguments?.getString("streamUrl") ?: ""
                val fixtureId = backStackEntry.arguments?.getString("fixtureId") ?: ""
                PlayerScreen(
                    streamUrl = streamUrl,
                    fixtureId = fixtureId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        if (showUpdateDialog && updateInfo != null) {
            UpdateDialog(
                version = updateInfo!!.version,
                onUpdate = {
                    showUpdateDialog = false
                    val checker = UpdateChecker(okhttp3.OkHttpClient())
                    checker.downloadAndInstall(context, updateInfo!!)
                },
                onDismiss = {
                    showUpdateDialog = false
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putLong(UpdateChecker.PREF_KEY_DISMISS_TIME, System.currentTimeMillis()).apply()
                }
            )
        }
    }
}
