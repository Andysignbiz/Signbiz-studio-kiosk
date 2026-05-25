package signbiz.kiosk.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
val SignbizBlackColor = Color(0xFF000000)

val GilroyFamily = FontFamily(
    Font(R.font.gilroy_regular, FontWeight.Normal),
    Font(R.font.gilroy_bold, FontWeight.Bold)
)

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current
    val kioskSettings = remember { KioskSettingsFactory.get(context) }
    var playerUrl by remember { mutableStateOf("") }
    var urlError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SignbizBlackColor)
    ) {
        // Centred content column
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Main heading — Gilroy Bold, ~80pt converted to sp
            Text(
                text = "Digital Signage",
                color = Color.White,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = GilroyFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle — Gilroy Regular, ~37pt converted to sp
            Text(
                text = "Link Your Screen by entering the URL found\nin the Signbiz Studio CMS under 'Screens'.",
                color = Color(0xFFBDBFC1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = GilroyFamily,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // URL input — Gilroy Regular, ~28pt converted to sp
            OutlinedTextField(
                value = playerUrl,
                onValueChange = {
                    playerUrl = it
                    urlError = false
                },
                placeholder = {
                    Text(
                        "Player URL",
                        color = Color(0xFF888888),
                        fontSize = 18.sp,
                        fontFamily = GilroyFamily,
                        fontWeight = FontWeight.Normal
                    )
                },
                isError = urlError,
                supportingText = if (urlError) {
                    {
                        Text(
                            "Please enter a valid URL starting with http",
                            color = Color.Red,
                            fontFamily = GilroyFamily
                        )
                    }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SignbizGreenColor,
                    unfocusedBorderColor = Color(0xFF333333),
                    cursorColor = SignbizGreenColor,
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF1A1A1A)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = GilroyFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.White
                ),
                singleLine = true
            )

            // Link Screen button — Gilroy Bold, ~35pt converted to sp
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
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SignbizGreenColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Link Screen",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GilroyFamily
                )
            }
        }

        // Signbiz Digital Studio logo — bottom right corner
        Image(
            painter = painterResource(id = R.drawable.signbiz_logo),
            contentDescription = "Signbiz Digital Studio",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .width(220.dp)
                .height(70.dp),
            contentScale = ContentScale.Fit
        )
    }
}
