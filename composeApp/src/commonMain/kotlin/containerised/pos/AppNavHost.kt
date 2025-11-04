package containerised.pos

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import containerised.pos.views.EditMenuPage
import containerised.pos.views.MenuPage
import containerised.pos.views.OrderPage

@Composable
fun AppNavHost() {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = "menu_list"
	) {
		composable("order") {
			OrderPage()
		}
		composable("menu_list") {
			MenuPage(navController)
		}
		composable(
			"edit/{itemId}",
			arguments = listOf(navArgument("itemId") { type = NavType.StringType })
		) { backStackEntry ->
			val itemId = backStackEntry.savedStateHandle.get<String>("itemId") ?: ""

			println("Resolved itemId = $itemId")

            EditMenuPage(navController = navController, itemId = itemId)
		}
	}
}
