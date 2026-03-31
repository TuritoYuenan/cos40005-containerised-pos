@file:OptIn(ExperimentalMaterial3Api::class)

package containerised.pos.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

@Composable
fun StaffTopBar(navController: NavController, currentRoute: String) {
	when {
		currentRoute == "inventory" -> InventoryTopBar()
		currentRoute.startsWith("ingredient-detail") -> IngredientDetailTopBar(navController)
		currentRoute.startsWith("stock-history") -> StockHistoryTopBar(navController)
		currentRoute.startsWith("sales-report") -> SalesReportTopBar(navController)
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
private fun SalesReportTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Sales Report") }
	)
}
