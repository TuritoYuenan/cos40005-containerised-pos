package containerised.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun EditMenuUI(navController: NavController, itemId: String) {
	var item by remember { mutableStateOf<MenuItem?>(null) }
	var error by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(Unit) {
		try {
			item = fetchMenuItemById(itemId)
			println("Fetched ${item!!} menuitem")

		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}

	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer)
				.padding(horizontal = 8.dp, vertical = 6.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Left: Menu button
			IconButton(onClick = { navController.navigate("menu_list") }) {
				Icon(
					imageVector = Icons.Default.ArrowBack,
					contentDescription = "Menu",
					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
		Column(
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
		) {
			Text(
				text = "Edit menu item",
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp, horizontal = 12.dp),
				style = MaterialTheme.typography.titleMedium.copy(
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface
				),
				textAlign = TextAlign.Start
			)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp, horizontal = 12.dp),
				horizontalArrangement = Arrangement.spacedBy(30.dp)
			) {
				if (!item?.img_url.isNullOrBlank()) {
					KamelImage(
						resource = asyncPainterResource(item!!.img_url!!),
						contentDescription = item!!.item_name,
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.size(64.dp)
							.clip(RoundedCornerShape(8.dp)),
						onFailure = {
							// fallback if the image fails to load
							Box(
								modifier = Modifier
									.size(120.dp)
									.clip(RoundedCornerShape(8.dp))
									.background(Color(0xFF0358AD)),
								contentAlignment = Alignment.Center
							) {}
						}
					)
				} else {
					// Blue placeholder if no image
					Box(
						modifier = Modifier
							.size(120.dp)
							.clip(RoundedCornerShape(8.dp))
							.background(Color(0xFF0358AD)),
						contentAlignment = Alignment.Center
					) {}
				}
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Button(
						onClick = { /* TODO: Handle image upload */ },
						shape = RoundedCornerShape(50),
						colors = androidx.compose.material3.ButtonDefaults.buttonColors(
							containerColor = Color(0xFF0358AD)
						),
						modifier = Modifier.height(40.dp)
					) {
						Text("Upload", color = Color.White)
					}
					Text(
						text = "Supports PNG, JPEG, WEBP images below 5MB",
						color = Color.Black.copy(alpha = 0.5f),
						style = MaterialTheme.typography.labelSmall.copy(
							fontSize = 12.sp
						)
					)
				}
			}
		}
	}
}
