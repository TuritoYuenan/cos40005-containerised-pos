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
		composable("edit/{itemId}") { backStackEntry ->
			val route = backStackEntry.destination.route
			val itemId = route?.substringAfter("edit/") ?: ""
			EditMenuUI(navController = navController, itemId = itemId)
		}
	}
}
