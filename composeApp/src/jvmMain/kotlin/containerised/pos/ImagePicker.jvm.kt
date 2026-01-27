package containerised.pos

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

@Composable
actual fun rememberImagePickerUri(
	onResult: (Any?) -> Unit
): () -> Unit {

	return {
		val dialog = java.awt.FileDialog(
			null as java.awt.Frame?,
			"Select Image",
			java.awt.FileDialog.LOAD
		)

		dialog.isVisible = true

		val file = dialog.file?.let {
			java.io.File(dialog.directory, it)
		}

		onResult(file)
	}
}
@Composable
actual fun rememberImagePickerBytes(
	uri: Any?
): ByteArray? {

	val file = uri as? java.io.File
	return file?.readBytes()
}
