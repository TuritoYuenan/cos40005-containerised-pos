@file:OptIn(ExperimentalMaterial3Api::class)

package containerised.pos.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

@Composable
fun StaffTopBar(navController: NavController, currentRoute: String?) {
	when {
		currentRoute == null -> GenericStaffTopBar()
		currentRoute == "inventory" -> InventoryTopBar()
		currentRoute.startsWith("ingredient-detail") -> IngredientDetailTopBar(navController)
		currentRoute.startsWith("stock-history") -> StockHistoryTopBar(navController)
		else -> GenericStaffTopBar()
	}
}

@Composable
private fun GenericStaffTopBar() {
	CenterAlignedTopAppBar(
		title = { Text(stringResource(Res.string.app_name)) }
	)
}

@Composable
private fun InventoryTopBar() {
	CenterAlignedTopAppBar(
		title = { Text("Inventory Management") }
	)
}

@Composable
private fun IngredientDetailTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Ingredient Details") }
	)
}

@Composable
private fun StockHistoryTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Stock Adjustment History") }
	)
}

@Composable
private fun BackButton(onClick: () -> Unit) {
	IconButton(
		onClick = { onClick() },
		modifier = Modifier.padding(start = 8.dp)
	) {
		Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
	}
}
