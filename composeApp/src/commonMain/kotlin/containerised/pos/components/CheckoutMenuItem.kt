package containerised.pos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import containerised.pos.models.MenuItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun CheckoutMenuItem(menuItem: MenuItem) {
	var value by remember { mutableStateOf(1) }
	Box {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterHorizontally),
		) {
			Box(
				modifier = Modifier
					.size(76.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0xFFACACAC)),
				contentAlignment = Alignment.Center
			) {}
			Column(
				modifier = Modifier
					.height(76.dp),

				verticalArrangement = Arrangement.SpaceEvenly
			) {
				Text(
					modifier = Modifier
						.width(82.dp),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					text = menuItem.name,
					style = MaterialTheme.typography.titleSmall
				)
				Text(
					text = menuItem.price.toString(),
					style = MaterialTheme.typography.bodySmall
				)
				Row {
					Text(
						text = "Total: $" + value*menuItem.price,
					)
				}
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier
					.height(76.dp),
			){
				Button(
					modifier = Modifier.size(32.dp),
					onClick = { value-- },
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				) {
					Icon(
						Icons.Filled.Remove,
						contentDescription = "Decrease"
					)
				}
				Text(
					text = value.toString(),
				)
				Button(
					modifier = Modifier
						.size(32.dp),
					onClick = { value++ },
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				){
					Icon(Icons.Filled.Add, contentDescription = "Decrease")
				}
			}
			Button(
				modifier = Modifier
					.size(24.dp),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0x00FFFFFF),
					contentColor = Color.Black
				),
				contentPadding = PaddingValues(0.dp)
			){
				Icon(Icons.Filled.Edit, contentDescription = "Edit")
			}
		}
	}
}
