package signbiz.kiosk.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity
import kotlinx.coroutines.launch
import signbiz.kiosk.R
import signbiz.kiosk.data.KioskSettingsFactory
import signbiz.kiosk.service.StayOnTopService

val SignbizGreenColor = Color(0xFF43B02A)
val SignbizBlackColor = Color(0xFF111111)

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current
    val kioskSettings = remember { KioskSettingsFactory.get(context) }
    var playerUrl by remember { mutableStateOf("") }
    var urlError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SignbizBlackColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Signbiz logo
            Image(
                painter = painterResource(id = R.drawable.signbiz_logo),
                contentDescription = "Signbiz Studio",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Welcome heading
            Text(
                text = "Welcome to Signbiz Studio",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Subheading
            Text(
                text = "Digital Signage",
                color = SignbizGreenColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instruction
            Text(
                text = "Enter the Player URL for this screen to get started.\nYou can find it in the Signbiz Studio CMS under Screens.",
                color = Color(0xFFBDBFC1),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // URL input
            OutlinedTextField(
                value = playerUrl,
                onValueChange = {
                    playerUrl = it
                    urlError = false
                },
                label = { Text("Player URL", color = Color(0xFFBDBFC1)) },
                placeholder = { Text("https://cms.signbiz-orders.co.nz/player.php?token=", color = Color(0xFF666666), fontSize = 12.sp) },
                isError = urlError,
                supportingText = if (urlError) {
                    { Text("Please enter a valid URL", color = Color.Red) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SignbizGreenColor,
                    unfocusedBorderColor = Color(0xFF666666),
                    cursorColor = SignbizGreenColor,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                ),
                singleLine = true
            )

            // Save button
            Button(
                onClick = {
                    val trimmed = playerUrl.trim()
                    if (trimmed.isEmpty() || !trimmed.startsWith("http")) {
                        urlError = true
                        return@Button
                    }
                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                        kioskSettings.setStartUrl(trimmed)
                        StayOnTopService.restart(context)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SignbizGreenColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save & Launch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer note
            Text(
                text = "Tap 5 times anywhere on screen while content is playing to access settings.",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
