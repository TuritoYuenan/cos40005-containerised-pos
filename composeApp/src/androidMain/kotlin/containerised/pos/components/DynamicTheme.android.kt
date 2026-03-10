package containerised.pos.components

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun dynamicColorSchemeOrNull(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme? {
	// Do not use dynamic color when disabled or not supported
    if (!dynamicColor || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return null
    }

    val context = LocalContext.current
    return if (darkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
}
