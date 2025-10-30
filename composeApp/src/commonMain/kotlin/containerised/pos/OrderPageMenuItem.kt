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
			data.item_name,
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
		item_id = "1",
		item_name = "Sample Item",
		item_des = "This is a sample item description.",
		price = 9.99f,
		category_id = "C1",
		is_available = true,
		est_prep_time = 5,
		img_url = "",
		branch_id = "B1",
		special_notes = null
	)
	OrderPageMenuItem(data = sampleItem)
}
