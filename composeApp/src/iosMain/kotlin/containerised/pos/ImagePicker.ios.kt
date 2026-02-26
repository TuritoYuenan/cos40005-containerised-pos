package containerised.pos

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerUri(
	onResult: (Any?) -> Unit
): () -> Unit {
	return {
		onResult(null)
	}
}

@Composable
actual fun rememberImagePickerBytes(
	uri: Any?
): ByteArray? {
	// does nothing
	return null
}
