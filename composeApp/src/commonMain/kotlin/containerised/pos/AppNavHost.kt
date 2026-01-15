package containerised.pos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.views.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost() {
	val navController = rememberNavController()

	MaterialTheme {
//		Staff-facing application, available on mobile and desktop
		if (!isWeb) {
			Scaffold(
				topBar = { StaffTopBar() },
				bottomBar = { StaffNavigationBar(navController) }
			) { paddingValues ->
				NavHost(
					navController = navController,
					startDestination = "login",
					modifier = Modifier.padding(paddingValues)
				) {
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
			}
		}

//		Customer-facing application, available on web only
		if (isWeb) {
			NavHost(
				navController = navController,
				startDestination = "order",
			) {
				composable("order") { CustomerOrderPage(navController) }
				composable("checkout") { CheckOutWebPage(navController) }
			}
		}
	}
}
