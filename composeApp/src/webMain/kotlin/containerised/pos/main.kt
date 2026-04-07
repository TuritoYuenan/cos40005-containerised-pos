package containerised.pos

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.AppTheme
import containerised.pos.routes.CustomerRoutes
import containerised.pos.views.CustomerCheckoutPage
import containerised.pos.views.CustomerOrderPage
import containerised.pos.views.CustomerPaymentPage
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
	ComposeViewport {
		val navController = rememberNavController()
		val startDestination = CustomerRoutes.Order("Unknown", "Unknown")
		val currentRoute = navController
			.currentBackStackEntryAsState()
			.value?.destination?.route.orEmpty()

		AppTheme {
			NavHost(navController, startDestination) {
				composable<CustomerRoutes.Order> { backStackEntry ->
					val args = backStackEntry.toRoute<CustomerRoutes.Order>()
					CustomerOrderPage(navController, args)
				}
				composable<CustomerRoutes.Checkout> { backStackEntry ->
					val args = backStackEntry.toRoute<CustomerRoutes.Checkout>()
					CustomerCheckoutPage(navController, args)
				}
				composable<CustomerRoutes.Payment> { backStackEntry ->
					val args = backStackEntry.toRoute<CustomerRoutes.Payment>()
					CustomerPaymentPage(navController, args)
				}
			}
		}

		LaunchedEffect(currentRoute) {
			window.document.title = when {
				currentRoute.startsWith("order") -> "Menu"
				currentRoute.startsWith("checkout") -> "Checkout"
				currentRoute.startsWith("payment") -> "Payment"
				else -> "Unknown"
			} + " - Containerised POS"
		}

		LaunchedEffect(navController) { navController.bindToBrowserNavigation() }
	}
}
