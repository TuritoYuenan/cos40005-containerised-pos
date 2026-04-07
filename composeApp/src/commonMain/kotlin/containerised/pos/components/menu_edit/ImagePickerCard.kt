package containerised.pos.components.menu_edit

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import containerised.pos.rememberImagePickerBytes
import containerised.pos.rememberImagePickerUri
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ImagePickerCard(
	imageBytes: ByteArray?,
	imageUrl: String?,
	onImageSelected: (ByteArray) -> Unit
) {
	var uri by remember { mutableStateOf<Any?>(null) }
	var pickedImageBytes  by remember { mutableStateOf<ByteArray?>(null) }
	val openImagePicker = rememberImagePickerUri { result ->
		println("Picked image: $result")
		uri = result
	}
	pickedImageBytes = imageBytes

	LaunchedEffect(uri) {
		pickedImageBytes = rememberImagePickerBytes(uri)
		pickedImageBytes?.let {
			onImageSelected(it) // upload AFTER conversion
		}
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp, horizontal = 12.dp),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 6.dp, horizontal = 12.dp),
			horizontalArrangement = Arrangement.spacedBy(60.dp)
		) {
			if (pickedImageBytes != null) {
				Image(
					bitmap = pickedImageBytes!!.decodeToImageBitmap(),
					contentDescription = null,
					modifier = Modifier
						.size(120.dp)
						.clip(RoundedCornerShape(8.dp))
				)
			}
			else if (imageUrl != null){
				val url = imageUrl.toString()
				KamelImage(
					resource = { asyncPainterResource(url) },
					contentDescription = "Menu image",
					modifier = Modifier
						.size(120.dp)
						.clip(RoundedCornerShape(8.dp))
				)
			}
			else {
				Box(
					modifier = Modifier
						.size(120.dp)
						.clip(RoundedCornerShape(8.dp))
						.background(Color(0xFFACACAC)),
					contentAlignment = Alignment.Center
				) {}
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Button(
					onClick = {
						openImagePicker()
					},
					shape = RoundedCornerShape(50),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = Color.White
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
