package containerised.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import containerised.pos.models.MenuItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OrderPageMenuItem(data: MenuItem) {
	val maxWidth = 128.dp
	Column(
		modifier = Modifier.padding(16.dp, 4.dp).width(maxWidth),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.size(maxWidth)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primary)
		) {
			// Placeholder for image or icon
		}
		Text(
			data.name,
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center,
		)
		Text(
			data.price.toString(),
			style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
	val sampleItem = MenuItem(
		id = "1",
		name = "Sample Item",
		description = "This is a sample item description.",
		price = 9.99f,
		categoryID = "C1",
		isAvailable = true,
		estimatedPreparationTime = 5,
		imageURL = "",
		branchID = "B1",
		specialNotes = null
	)
	OrderPageMenuItem(data = sampleItem)
}
