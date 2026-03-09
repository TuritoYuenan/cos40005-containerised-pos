package containerised.pos.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun BackButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier) {
	Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
}
