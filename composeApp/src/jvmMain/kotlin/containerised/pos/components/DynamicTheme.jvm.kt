package containerised.pos.components

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicColorSchemeOrNull(
	darkTheme: Boolean,
	dynamicColor: Boolean
): ColorScheme? {
	println("darkTheme=$darkTheme, dynamicColor=$dynamicColor - Dynamic theming is not supported on JVM.")
	return null
}
