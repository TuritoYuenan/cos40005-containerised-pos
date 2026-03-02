package containerised.pos

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberImagePickerUri(onResult: (Any?) -> Unit): () -> Unit {
	return {
		val dialog = FileDialog(
			null as Frame?,
			"Select Image",
			FileDialog.LOAD
		)

		dialog.isVisible = true

		val file = dialog.file?.let { File(dialog.directory, it) }
		onResult(file)
	}
}

@Composable
actual fun rememberImagePickerBytes(uri: Any?): ByteArray? = (uri as? File)?.readBytes()
