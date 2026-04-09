package containerised.pos.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SettingsButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier.testTag("settingsButton")) {
	Icon(Icons.Default.Settings, "Settings")
}

@Preview
@Composable
fun BackButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier.testTag("backButton")) {
	Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
}

@Preview
@Composable
fun HomeButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier.testTag("homeButton")) {
	Icon(Icons.Default.Home, "Home")
}

@Preview
@Composable
fun AddToCartButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
) = FilledTonalIconButton(
	onClick,
	modifier.padding(8.dp).size(32.dp).testTag("addToCartButton")
) {
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
	modifier.testTag("cartFab"),
	containerColor = MaterialTheme.colorScheme.primary,
	contentColor = MaterialTheme.colorScheme.onPrimary,
)

@Composable
fun CreateButton(onClick: () -> Unit) = Button(
	onClick,
	Modifier.height(40.dp).testTag("createButton"),
	shape = RoundedCornerShape(8.dp),
	colors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = Color.White
	)
) {
	Icon(Icons.Filled.Check, contentDescription = "Create")
	Spacer(Modifier.width(6.dp))
	Text("Create")
}

@Composable
fun UpdateButton(onClick: () -> Unit) = Button(
	onClick = onClick,
	modifier = Modifier.height(40.dp).testTag("updateButton"),
	shape = RoundedCornerShape(8.dp),
	colors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = Color.White
	)
) {
	Icon(Icons.Filled.Check, "Update")
	Spacer(Modifier.width(6.dp))
	Text("Update")
}

@Composable
fun DeleteButton(onClick: () -> Unit) = Button(
	onClick = onClick,
	shape = RoundedCornerShape(8.dp),
	colors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.colorScheme.error,
		contentColor = Color.White
	),
	modifier = Modifier.height(40.dp).testTag("deleteButton")
) {
	Icon(Icons.Filled.Delete, "Delete")
	Spacer(Modifier.width(6.dp))
	Text("Delete")
}
