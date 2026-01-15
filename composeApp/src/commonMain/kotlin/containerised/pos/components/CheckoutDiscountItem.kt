package containerised.pos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun CheckoutDiscountItem() {
		Row(
			modifier = Modifier
				.clip(RoundedCornerShape(8.dp))
				.background(MaterialTheme.colorScheme.outlineVariant)
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Box(
				modifier = Modifier
					.size(52.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0xFFACACAC)),
				contentAlignment = Alignment.Center
			) {}
			Column(
				modifier = Modifier
					.height(52.dp),

				verticalArrangement = Arrangement.SpaceEvenly
			) {
				Text(
					text = "Lorem isum title",
					style = MaterialTheme.typography.titleSmall
				)
				Text(
					text = "Lorem isum condition",
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
	}

