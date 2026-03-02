package containerised.pos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberImagePickerUri(onResult: (Any?) -> Unit): () -> Unit {
	val picker = rememberLauncherForActivityResult(
		ActivityResultContracts.PickVisualMedia()
	) { uri ->
		onResult(uri) // android.net.Uri, but exposed as Any?
	}

	return {
		picker.launch(
			PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
		)
	}
}

@Composable
actual fun rememberImagePickerBytes(uri: Any?): ByteArray? {
	val context = LocalContext.current
	val androidUri = uri as? android.net.Uri

	return androidUri?.let {
		context.contentResolver
			.openInputStream(it)
			?.use { stream -> stream.readBytes() }
	}
}
