package containerised.pos

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavHost() {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = "menu_list"
	) {
		composable("menu_list") {
			MenuUI(navController)
		}
		composable(
			"edit/{itemId}",
			arguments = listOf(navArgument("itemId") { type = NavType.StringType })
		) { backStackEntry ->
			val itemId = backStackEntry.savedStateHandle.get<String>("itemId") ?: ""

			println("Resolved itemId = $itemId")

			EditMenuUI(navController = navController, itemId = itemId)
		}
	}
}
