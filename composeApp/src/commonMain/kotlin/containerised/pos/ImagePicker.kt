package containerised.pos

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerUri(onResult: (Any?) -> Unit): () -> Unit

@Composable
expect fun rememberImagePickerBytes(uri: Any?): ByteArray?
