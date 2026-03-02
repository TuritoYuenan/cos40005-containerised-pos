package containerised.pos

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerUri(onResult: (Any?) -> Unit): () -> Unit = { onResult(null) }

@Composable
actual fun rememberImagePickerBytes(uri: Any?): ByteArray? = null
