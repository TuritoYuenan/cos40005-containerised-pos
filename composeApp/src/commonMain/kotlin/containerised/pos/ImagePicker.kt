package containerised.pos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
expect fun rememberImagePickerUri(
	onResult: (Any?) -> Unit
): () -> Unit

@Composable
expect fun rememberImagePickerBytes(
	uri: Any?
): ByteArray?
