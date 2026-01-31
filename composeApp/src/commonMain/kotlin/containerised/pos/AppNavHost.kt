package containerised.pos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.models.CustomerPayment
import containerised.pos.views.CheckOutWebPage
import containerised.pos.views.CustomerOrderPage
import containerised.pos.views.EditItemPage
import containerised.pos.views.EditPromotionPage
import containerised.pos.views.EditTagPage
import containerised.pos.views.CustomerPaymentPage
import containerised.pos.views.LoginPage
import containerised.pos.views.MenuEditPage
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
					//Change back to "login" before merging
					startDestination = "menu-edit",
					modifier = Modifier.padding(paddingValues)
				) {
					composable("login") { LoginPage() }
					composable("menu-edit") { MenuEditPage() }
					composable("edit-item") { EditItemPage(navController) }
					composable("edit-tag") { EditTagPage(navController) }
					composable("edit-promotion") { EditPromotionPage(navController) }
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
				composable<CustomerPayment> { backStackEntry ->
					val customerPayment = backStackEntry.toRoute<CustomerPayment>()
					CustomerPaymentPage(navController, customerPayment)
				}
			}
		}
	}
}
