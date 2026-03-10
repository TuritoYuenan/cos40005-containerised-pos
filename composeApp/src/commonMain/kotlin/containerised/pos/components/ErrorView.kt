package containerised.pos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorView(message: String, modifier: Modifier) {
	Column(
		modifier,
		verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			Icons.Default.Error,
			contentDescription = "Error",
			tint = MaterialTheme.colorScheme.error,
			modifier = Modifier.size(96.dp)
		)
		Text(
			message,
			color = MaterialTheme.colorScheme.error,
			style = MaterialTheme.typography.headlineSmall
		)
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ErrorViewPreview() {
	ErrorView(
		"This is an error message",
		Modifier.fillMaxSize().padding(16.dp)
	)
}
