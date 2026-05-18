package signbiz.kiosk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import signbiz.kiosk.app.FullScreenHelper
import signbiz.kiosk.app.NotificationPermissionHelper
import signbiz.kiosk.app.StayOnTopServiceStarter
import signbiz.kiosk.app.TapUnlockHandler
import signbiz.kiosk.components.MainScreen
import signbiz.kiosk.components.TouchKioskInputOverlay
import signbiz.kiosk.components.TvKioskInputOverlay
import signbiz.kiosk.ui.theme.SignbizKioskTheme
import signbiz.kiosk.ui.theme.isTvDevice

class MainActivity : ComponentActivity() {
    private lateinit var unlockHandler: TapUnlockHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FullScreenHelper.enableImmersiveMode(this.window)
        StayOnTopServiceStarter.ensureRunning(this)

        unlockHandler = TapUnlockHandler {
            openSettings()
        }

        setContent {
            SignbizKioskTheme {
                AppContent(unlockHandler, this)
            }
        }
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}

@Composable
fun AppContent(unlockHandler: TapUnlockHandler, activity: Activity) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("MainActivity", "Notification permission granted: $isGranted")
    }

    LaunchedEffect(Unit) {
        if (!NotificationPermissionHelper.hasPermission(context)) {
            NotificationPermissionHelper.requestPermission(permissionLauncher)
        }
    }

    val isTv = isTvDevice()

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        MainScreen(activity = activity, modifier = Modifier.fillMaxSize())

        if (isTv) {
            TvKioskInputOverlay(onTap = {
                unlockHandler.registerTap()
            })
        } else {
            TouchKioskInputOverlay(
                onTap = { unlockHandler.registerTap() },
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
    }
}