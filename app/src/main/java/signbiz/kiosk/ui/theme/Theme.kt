package signbiz.kiosk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SignbizKioskTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        darkColorScheme(
            primary = SignbizGreen,
            secondary = SignbizLightGreen,
            tertiary = SignbizLightGrey
        )
    } else {
        lightColorScheme(
            primary = SignbizGreen,
            secondary = SignbizLightGreen,
            tertiary = SignbizDarkGrey
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}