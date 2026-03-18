package containerised.pos.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Custom theme for the POS application
 * - Uses Material 3 theming
 * - Supports dynamic color on Android 12+ if enabled
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val staticScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    val dynamicScheme = dynamicColorSchemeOrNull(darkTheme, dynamicColor)
    MaterialTheme(dynamicScheme ?: staticScheme, content = content)
}

/**
 * Returns a dynamic [ColorScheme] if dynamic color is supported and enabled, or null otherwise.
 */
@Composable
expect fun dynamicColorSchemeOrNull(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme?
