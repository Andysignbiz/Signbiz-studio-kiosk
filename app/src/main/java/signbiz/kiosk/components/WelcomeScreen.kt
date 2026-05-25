package signbiz.kiosk.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import signbiz.kiosk.R
import signbiz.kiosk.SettingsActivity

val SignbizGreenColor = Color(0xFF43B02A)
val SignbizBlackColor = Color(0xFF000000)

val GilroyFamily = FontFamily(
    Font(R.font.gilroy_regular, FontWeight.Normal),
    Font(R.font.gilroy_bold, FontWeight.Bold)
)

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Main heading
            Text(
                text = "Digital Signage",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = GilroyFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle — single line, no forced breaks
            Text(
                text = "Link Your Screen by entering the URL found in the Signbiz Studio CMS under 'Screens'.",
                color = Color(0xFFBDBFC1),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = GilroyFamily,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Button — launches Settings screen where URL entry works with TV remote
            Button(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SignbizGreenColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Link Screen",
                    fontSize = 18.sp,
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
                .padding(28.dp)
                .width(180.dp)
                .height(56.dp),
            contentScale = ContentScale.Fit
        )
    }
}
