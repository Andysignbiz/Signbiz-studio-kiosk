package signbiz.kiosk.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import signbiz.kiosk.R
import signbiz.kiosk.data.KioskSettingsFactory
import signbiz.kiosk.data.Rotation
import signbiz.kiosk.service.StayOnTopService

private const val DEFAULT_URL = "https://cms.signbiz-orders.co.nz/player.php?token="

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val kioskSettings = remember { KioskSettingsFactory.get(context) }

    var kioskUrl by remember { mutableStateOf("") }
    var checkIntervalSeconds by remember { mutableStateOf("") }
    var rotation by remember { mutableStateOf(Rotation.ROTATION_0) }

    var checkIntervalError by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("General", "Display")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val savedUrl = kioskSettings.getStartUrl().first()
        // Pre-fill base URL if nothing has been saved yet
        kioskUrl = if (savedUrl.isEmpty()) DEFAULT_URL else savedUrl
        checkIntervalSeconds = (kioskSettings.getCheckInterval().first() / 1000).toString()
        rotation = kioskSettings.getRotation().first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 48.dp, vertical = 24.dp)  // outer margins on all sides
        ) {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex, edgePadding = 0.dp) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> GeneralSettingsTab(
                    kioskUrl = kioskUrl,
                    onKioskUrlChange = { kioskUrl = it },
                    checkIntervalSeconds = checkIntervalSeconds,
                    onCheckIntervalChange = { checkIntervalSeconds = it },
                    checkIntervalError = checkIntervalError,
                    onCheckIntervalErrorChange = { checkIntervalError = it }
                )
                1 -> DisplaySettingsTab(
                    rotation = rotation,
                    onRotationChange = { rotation = it }
                )
            }

            Spacer(Modifier.weight(1f))

            // Buttons row — pushed to bottom with comfortable margin from edge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                FocusableButton(
                    text = stringResource(R.string.button_cancel),
                    onClick = { (context as? ComponentActivity)?.finish() },
                    background = MaterialTheme.colorScheme.surface
                )

                FocusableButton(
                    text = stringResource(R.string.button_save),
                    onClick = {
                        var hasError = false

                        val checkIntervalValue = checkIntervalSeconds.toLongOrNull()
                        if (checkIntervalSeconds.isBlank()) { checkIntervalError = "Required"; hasError = true }
                        else if (checkIntervalValue == null || checkIntervalValue !in 1..99999) { checkIntervalError = "Invalid"; hasError = true }

                        if (hasError) return@FocusableButton

                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            kioskSettings.setCheckInterval(checkIntervalValue!! * 1000L)
                            kioskSettings.setStartUrl(kioskUrl)
                            kioskSettings.setRotation(rotation)

                            StayOnTopService.restart(context)
                        }
                        (context as? ComponentActivity)?.finish()
                    },
                    background = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GeneralSettingsTab(
    kioskUrl: String,
    onKioskUrlChange: (String) -> Unit,
    checkIntervalSeconds: String,
    onCheckIntervalChange: (String) -> Unit,
    checkIntervalError: String?,
    onCheckIntervalErrorChange: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        SettingsField(
            label = stringResource(R.string.settings_kiosk_url_label),
            description = stringResource(R.string.settings_kiosk_url_desc),
            value = kioskUrl,
            onValueChange = onKioskUrlChange,
            placeholder = stringResource(R.string.settings_kiosk_url_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )

        Spacer(Modifier.height(32.dp))

        SettingsField(
            label = stringResource(R.string.settings_check_interval_label),
            description = stringResource(R.string.settings_check_interval_desc),
            value = checkIntervalSeconds,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("\\d*"))) {
                    onCheckIntervalChange(newValue)
                    onCheckIntervalErrorChange(null)
                }
            },
            placeholder = stringResource(R.string.settings_check_interval_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = checkIntervalError != null,
            supportingText = checkIntervalError ?: stringResource(R.string.settings_check_interval_supporting)
        )
    }
}

@Composable
fun DisplaySettingsTab(
    rotation: Rotation,
    onRotationChange: (Rotation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        RotationSelector(rotation = rotation, onRotationChange = onRotationChange)
    }
}
