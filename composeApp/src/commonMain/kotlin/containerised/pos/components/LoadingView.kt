package containerised.pos.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
	Box(modifier, Alignment.Center) {
		CircularProgressIndicator()
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoadingIndicatorPreview() {
	LoadingView(Modifier.fillMaxSize())
}
