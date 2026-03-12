package containerised.pos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.AppTheme
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.models.Order
import containerised.pos.models.OrderStatus
import containerised.pos.routes.StaffRoutes
import containerised.pos.views.*
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost() {
	val navController = rememberNavController()

//	Must change "order" to "login" when auth is implemented
	val startDestination = StaffRoutes.Inventory

	AppTheme {
//		Staff-facing application, available on mobile and desktop
		val currentRoute = navController
			.currentBackStackEntryAsState()
			.value?.destination?.route
			.orEmpty()

		LaunchedEffect(Unit) {
			OrderRealtimeManager.events.collect { action ->
				when (action) {
					is PostgresAction.Insert -> {
						val newOrder = action.decodeRecord<Order>()
						println("Insert data: $newOrder")
						NotificationService.showNotification(
							title = "New Order",
							message = "Order #${newOrder.orderNumber} received"
						)
					}

					is PostgresAction.Update -> {
						val updated = action.decodeRecord<Order>()
						val old = action.decodeOldRecord<Order>()
						if (old.status == OrderStatus.PREPARING && updated.status == OrderStatus.FINISHED) {
							println("deleted data: $old")
							NotificationService.showNotification(
								title = "Order Updated",
								message = "Order #${old.orderNumber} is done"
							)
						} else if (old.status == OrderStatus.PREPARING && updated.status == OrderStatus.CANCELED) {
							println("deleted data: $old")
							NotificationService.showNotification(
								title = "Order Updated",
								message = "Order #${old.orderNumber} is canceled"
							)
						} else if ((old.status == OrderStatus.FINISHED || old.status == OrderStatus.CANCELED) && updated.status == OrderStatus.PREPARING) {
							println("Insert data: $updated")
						} else {
							println("old data: $old")
							println("Updated data: $updated")
						}
					}

					is PostgresAction.Delete -> {
						val old = action.decodeOldRecord<Order>()
						println("Deleted → id=${old.orderId}")
					}

					is PostgresAction.Select -> {

					}
				}
			}
		}

		Scaffold(
			topBar = { StaffTopBar(navController, currentRoute) },
			bottomBar = { StaffNavigationBar(navController, startDestination) }
		) { paddingValues ->
			NavHost(navController, startDestination, Modifier.padding(paddingValues)) {
				composable<StaffRoutes.Login> { LoginPage() }
				composable<StaffRoutes.MenuEdit> { MenuEditPage() }
				composable<StaffRoutes.EditItem> { EditItemPage(navController) }
				composable<StaffRoutes.EditTag> { EditTagPage(navController) }
				composable<StaffRoutes.EditPromotion> { EditPromotionPage(navController) }
				composable<StaffRoutes.KitchenDisplay> { KitchenDisplayPage(navController) }
				composable<StaffRoutes.Inventory> { InventoryPage(navController) }
				composable<StaffRoutes.IngredientDetail> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.IngredientDetail>()
					EditIngredientPage(navController, args)
				}
				composable<StaffRoutes.StockHistory> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.StockHistory>()
					StockHistoryPage(args)
				}
			}
		}
	}
}
