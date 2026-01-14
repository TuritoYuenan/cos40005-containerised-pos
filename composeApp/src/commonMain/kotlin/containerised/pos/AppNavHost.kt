package containerised.pos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import containerised.pos.database.Database
import containerised.pos.views.CustomerOrderPage
import containerised.pos.views.EditMenuPage
import containerised.pos.views.LoginPage
import containerised.pos.views.MenuPage
import containerised.pos.views.OrderPage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.app_name
import posapplication.composeapp.generated.resources.baseline_menu_24
import posapplication.composeapp.generated.resources.compose_multiplatform

data class NavigationItem(val label: String, val route: String)

val navItems = listOf(
	NavigationItem("Order", "order"),
	NavigationItem("Menu", "menu_list"),
	NavigationItem("Kitchen", "kitchen"),
	NavigationItem("Sales", "sales"),
	NavigationItem("Inventory", "inventory")
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost() {
//	val cacheDatabase = Database(DatabaseDriverFactory())
	val navController = rememberNavController()
	val startDestination = if (isWeb) "order" else "login"
	var selectedDestination by rememberSaveable { mutableStateOf("login") }

	MaterialTheme {
		Scaffold(
			topBar = {
				if (!isWeb) CenterAlignedTopAppBar(
					title = { Text(stringResource(Res.string.app_name)) },
					navigationIcon = {
						IconButton(onClick = {}) {
							Icon(
								imageVector = vectorResource(Res.drawable.baseline_menu_24),
								contentDescription = "Menu",
								modifier = Modifier,
								tint = MaterialTheme.colorScheme.onSurface
							)
						}
					},
					actions = {
						IconButton(onClick = {}) {
//							Icon(imageVector = vectorResource(Res.drawable.compose_multiplatform))
						}
					},
				)
			},
			bottomBar = {
				if (!isWeb) NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
					for (item in navItems) {
						NavigationBarItem(
							selected = selectedDestination == item.route,
							label = { Text(item.label) },
							icon = {},
							onClick = {
								navController.navigate(item.route)
								selectedDestination = item.route
							},
						)
					}
				}
			}
		) { paddingValues ->
			NavHost(
				navController = navController,
				startDestination = startDestination,
				modifier = Modifier.padding(paddingValues)
			) {
				if (!isWeb) {
					composable("login") { LoginPage() }
					composable("order") { OrderPage(PaddingValues()) }
					composable("menu_list") { MenuPage(navController) }
					composable(
						"menu/{itemId}/edit",
						arguments = listOf(navArgument("itemId") { type = NavType.StringType })
					) { backStackEntry ->
						val itemId = backStackEntry.savedStateHandle.get<String>("itemId") ?: ""
						println("Resolved itemId = $itemId")
						EditMenuPage(navController = navController, itemId = itemId)
					}
				}

				if (isWeb) {
					composable("order") { CustomerOrderPage(navController) }
				}
			}
		}
	}
}
