package containerised.pos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.routes.CustomerRoutes
import containerised.pos.routes.StaffRoutes
import containerised.pos.views.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost(onNavHostReady: suspend (NavController) -> Unit = {}) {
	val navController = rememberNavController()

//	Must change "order" to "login" when auth is implemented
	val startDestination = if (isWeb) CustomerRoutes.Order("N/A") else StaffRoutes.KitchenDisplay

	MaterialTheme {
//		Staff-facing application, available on mobile and desktop
		if (!isWeb) {
			Scaffold(
				topBar = { StaffTopBar() },
				bottomBar = { StaffNavigationBar(navController, startDestination) }
			) { paddingValues ->
				NavHost(
					navController = navController,
					startDestination = startDestination,
					modifier = Modifier.padding(paddingValues)
				) {
					composable<StaffRoutes.Login> { LoginPage() }
					composable<StaffRoutes.MenuEdit> { MenuEditPage() }
					composable<StaffRoutes.EditItem> { EditItemPage(navController) }
					composable<StaffRoutes.EditTag> { EditTagPage(navController) }
					composable<StaffRoutes.EditPromotion> { EditPromotionPage(navController) }
					composable<StaffRoutes.KitchenDisplay> { KitchenDisplayPage(navController) }
				}
			}
		}

//		Customer-facing application, available on web only
		if (isWeb) {
			NavHost(
				navController = navController,
				startDestination = startDestination,
			) {
				composable<CustomerRoutes.Order> { backStackEntry ->
					val args = backStackEntry.toRoute<CustomerRoutes.Order>()
					CustomerOrderPage(navController, args)
				}
				composable<CustomerRoutes.Checkout> { CustomerCheckoutPage(navController) }
				composable<CustomerRoutes.Payment> { backStackEntry ->
					val args = backStackEntry.toRoute<CustomerRoutes.Payment>()
					CustomerPaymentPage(navController, args)
				}
			}
		}
	}

	LaunchedEffect(navController) { onNavHostReady(navController) }
}
