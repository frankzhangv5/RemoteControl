package zhang.feng.remotecontrol.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val PrimaryColor = Color(0xFF008D4E)
private val PrimaryLight = Color(0xFF4DBF7F)
private val PrimaryDark = Color(0xFF005D2E)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA0E6C1),
    onPrimaryContainer = Color(0xFF002114),

    secondary = Color(0xFF4D6357),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF0A1F17),

    tertiary = Color(0xFF3E6472),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC1E9FA),
    onTertiaryContainer = Color(0xFF003546),

    background = Color(0xFFFBFDF8),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFFBFDF8),
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFDCE5DC),
    onSurfaceVariant = Color(0xFF404943),

    outline = Color(0xFF707973),
    inverseSurface = Color(0xFF2E312F),
    inverseOnSurface = Color(0xFFEFF1ED),
    inversePrimary = Color(0xFF7DD3A1),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    scrim = Color.Black.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Color(0xFF003824),
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Color(0xFFA0E6C1),

    secondary = Color(0xFFB3CCBD),
    onSecondary = Color(0xFF1F352A),
    secondaryContainer = Color(0xFF354B40),
    onSecondaryContainer = Color(0xFFCFE9D9),

    tertiary = Color(0xFFA5CDDE),
    onTertiary = Color(0xFF0D4C60),
    tertiaryContainer = Color(0xFF275B6F),
    onTertiaryContainer = Color(0xFFC1E9FA),

    background = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF191C1A),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C1),

    outline = Color(0xFF8A938C),
    inverseSurface = Color(0xFFE1E3DF),
    inverseOnSurface = Color(0xFF2E312F),
    inversePrimary = PrimaryColor,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    scrim = Color.Black.copy(alpha = 0.5f)
)


//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40,
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

@Composable
fun RemoteControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}