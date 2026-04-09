package containerised.pos.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SettingsButton(
	modifier: Modifier = Modifier.padding(start = 8.dp),
	onClick: () -> Unit = {}
) = IconButton({ onClick() }, modifier) {
	Icon(Icons.Default.Settings, "Settings")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
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
}

@Composable
fun UpdateButton(onClick: () -> Unit) = Button(
    onClick = onClick,
    modifier = Modifier.height(40.dp),
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
    modifier = Modifier.height(40.dp)
) {
    Icon(Icons.Filled.Delete, "Delete")
    Spacer(Modifier.width(6.dp))
    Text("Delete")
}
