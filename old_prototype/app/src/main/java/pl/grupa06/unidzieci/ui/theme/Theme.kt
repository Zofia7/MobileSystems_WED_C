package pl.grupa06.unidzieci.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// bez dynamic color, żeby kolory były takie same na każdym telefonie

private val LightColorScheme = lightColorScheme(
    primary = Bordo,
    onPrimary = Color.White,
    primaryContainer = BordoJasne,
    onPrimaryContainer = BordoCiemne,
    secondary = Color(0xFF5E5557),
    secondaryContainer = Color(0xFFEDE6E3),
    onSecondaryContainer = Atrament,
    tertiaryContainer = Color(0xFFFFF0C7),
    onTertiaryContainer = Color(0xFF473600),
    background = Papier,
    onBackground = Atrament,
    surface = Papier,
    onSurface = Atrament,
    surfaceVariant = Color(0xFFF1EDE8),
    onSurfaceVariant = Color(0xFF574F51),
    outline = Color(0xFF8A8082),
    outlineVariant = Color(0xFFE2DBD6),
)

private val DarkColorScheme = darkColorScheme(
    primary = BordoNaCiemnym,
    onPrimary = Color(0xFF561D23),
    primaryContainer = Color(0xFF72222E),
    onPrimaryContainer = Color(0xFFFFDADB),
    secondary = Color(0xFFD7C1C3),
    secondaryContainer = Color(0xFF3A3233),
    onSecondaryContainer = Color(0xFFF1DEDF),
    tertiaryContainer = Color(0xFF4A3D10),
    onTertiaryContainer = Color(0xFFF6E2A6),
    background = PapierCiemny,
    onBackground = Color(0xFFECE0E0),
    surface = PapierCiemny,
    onSurface = Color(0xFFECE0E0),
    surfaceVariant = Color(0xFF2A2425),
    onSurfaceVariant = Color(0xFFD2C3C4),
    outline = Color(0xFF9C8D8E),
    outlineVariant = Color(0xFF4A4244),
)

@Composable
fun UniDzieciTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
