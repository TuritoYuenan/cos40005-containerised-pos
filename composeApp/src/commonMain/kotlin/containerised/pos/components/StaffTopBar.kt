@file:OptIn(ExperimentalMaterial3Api::class)

package containerised.pos.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import containerised.pos.routes.StaffRoutes
import org.jetbrains.compose.resources.stringResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name

@Composable
fun StaffTopBar(navController: NavController, currentRoute: String) {
	when {
		currentRoute == "inventory" -> InventoryTopBar(navController)
		currentRoute.startsWith("edit-ingredient") -> IngredientDetailTopBar(navController)
		currentRoute.startsWith("edit-promotion") -> EditPromotionTopBar(navController)
		currentRoute.startsWith("edit-item") -> EditItemTopBar(navController)
		currentRoute.startsWith("edit-tag") -> EditTagTopBar(navController)
		currentRoute.startsWith("stock-history") -> StockHistoryTopBar(navController)
		currentRoute.startsWith("sales-report") -> SalesReportTopBar(navController)
		currentRoute.startsWith("employee-profile") -> {}
		currentRoute.startsWith("setting") -> GenericStaffTopBar()
		else -> GenericStaffTopBar(navController)
	}
}

@Composable
private fun GenericStaffTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		title = { Text(stringResource(Res.string.app_name)) },
		// Right-side icon buttons
		actions = {
			SettingsButton { navController.navigate(StaffRoutes.Setting) }
		}
	)
}

@Composable
private fun GenericStaffTopBar() {
	CenterAlignedTopAppBar(
		title = { Text(stringResource(Res.string.app_name)) },
	)
}

@Composable
private fun InventoryTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		title = { Text("Inventory Management") },
		// Right-side icon buttons
		actions = {
			SettingsButton { navController.navigate(StaffRoutes.Setting) }
		}
	)
}

@Composable
private fun IngredientDetailTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Ingredient Details") },
		// Right-side icon buttons
		actions = {
			SettingsButton { navController.navigate(StaffRoutes.Setting) }
		}
	)
}

@Composable
private fun StockHistoryTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Stock Adjustment History") },
		// Right-side icon buttons
		actions = {
			SettingsButton { navController.navigate(StaffRoutes.Setting) }
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPromotionTopBar(navController: NavController) = CenterAlignedTopAppBar(
	navigationIcon = { BackButton { navController.popBackStack() } },
	title = { Text("Edit Promotion") },
	// Right-side icon buttons
	actions = {
		SettingsButton { navController.navigate(StaffRoutes.Setting) }
	},
	modifier = Modifier.testTag("topBar")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditItemTopBar(navController: NavController) = CenterAlignedTopAppBar(
	navigationIcon = { BackButton { navController.popBackStack() } },
	title = { Text("Edit Item") },
	// Right-side icon buttons
	actions = {
		SettingsButton { navController.navigate(StaffRoutes.Setting) }
	},
	modifier = Modifier.testTag("topBar")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTagTopBar(navController: NavController) = CenterAlignedTopAppBar(
	navigationIcon = { BackButton { navController.popBackStack() } },
	title = { Text("Edit Tag") },
	// Right-side icon buttons
	actions = {
		SettingsButton { navController.navigate(StaffRoutes.Setting) }
	},
	modifier = Modifier.testTag("topBar")
)

@Composable
private fun SalesReportTopBar(navController: NavController) {
	CenterAlignedTopAppBar(
		navigationIcon = { BackButton { navController.popBackStack() } },
		title = { Text("Sales Report") }
	)
}
