package containerised.pos.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SettingsButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier) {
	Icon( imageVector = Icons.Default.Settings, contentDescription = "Settings")
}
@Preview
@Composable
fun BackButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier) {
	Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
}

@Preview
@Composable
fun HomeButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier) {
	Icon(Icons.Default.Home, "Home")
}

@Preview
@Composable
fun AddToCartButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
) = FilledTonalIconButton(onClick, modifier.padding(8.dp).size(32.dp)) {
	Icon(Icons.Filled.Add, "Add to cart", Modifier.size(18.dp))
}

@Preview
@Composable
fun CartFAB(
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
) = ExtendedFloatingActionButton(
	{ Text("View Cart") },
	{ Icon(Icons.Filled.ShoppingCart, "Cart") },
	onClick,
	modifier,
	containerColor = MaterialTheme.colorScheme.primary,
	contentColor = MaterialTheme.colorScheme.onPrimary,
)
