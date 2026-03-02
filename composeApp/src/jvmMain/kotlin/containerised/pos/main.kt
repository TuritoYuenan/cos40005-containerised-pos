package containerised.pos

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "Containerised POS",
		state = rememberWindowState(
			position = WindowPosition.Aligned(Alignment.Center),
			width = 450.dp, height = 850.dp
		),
	) { AppNavHost() }
}
