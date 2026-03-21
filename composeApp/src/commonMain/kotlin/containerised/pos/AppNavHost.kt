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
	val startDestination = StaffRoutes.KitchenDisplay

	AppTheme {
//		Staff-facing application, available on mobile and desktop
		val currentRoute = navController
			.currentBackStackEntryAsState()
			.value?.destination?.route.orEmpty()
		LaunchedEffect(Unit) {
			RealtimeServiceController.start()
			RealtimeManager.forOrders.events.collect { action ->
				when (action) {
					is PostgresAction.Insert -> action.handle()
					is PostgresAction.Update -> action.handle()
					is PostgresAction.Delete -> action.handle()
					is PostgresAction.Select -> {}
				}
			}
		}

		Scaffold(
			topBar = { StaffTopBar(navController, currentRoute) },
			bottomBar = { StaffNavigationBar(navController, startDestination) }
		) { paddingValues ->
			NavHost(navController, startDestination, Modifier.padding(paddingValues)) {
				composable<StaffRoutes.Login> { LoginPage() }
				composable<StaffRoutes.MenuEdit> { MenuEditPage(navController) }
				composable<StaffRoutes.EditItem> { backStackEntry ->
                        val args = backStackEntry.toRoute<StaffRoutes.EditItem>()
                        EditItemPage(navController, args.itemId)
                    }
				composable<StaffRoutes.EditTag> { backStackEntry ->
                        val args = backStackEntry.toRoute<StaffRoutes.EditTag>()
                        EditTagPage(navController, args.tagId)}
                    }
				composable<StaffRoutes.EditPromotion> { backStackEntry ->
                        val args = backStackEntry.toRoute<StaffRoutes.EditPromotion>()
                        EditPromotionPage(navController, args.promotionId)
                    }
				composable<StaffRoutes.KitchenDisplay> { KitchenDisplayPage() }
				composable<StaffRoutes.OrderConfirm> { OrderConfirmPage() }
				composable<StaffRoutes.Inventory> { InventoryPage(navController) }
				composable<StaffRoutes.EditIngredient> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.EditIngredient>()
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

private fun PostgresAction.Insert.handle() {
	val new = this.decodeRecord<Order>()
	println("Insert data: $new")

	NotificationService.showNotification(
		title = "New Order",
		message = "Order #${new.orderNumber} received"
	)
}

private fun PostgresAction.Delete.handle() {
	println("Deleted → Order id=${decodeOldRecord<Order>().orderId}")
}

private fun PostgresAction.Update.handle() {
	val new = this.decodeRecord<Order>()
	val old = this.decodeOldRecord<Order>()
	val (isPtoF, isPtoC, isFCtoP) = new.inferStatusChange(old)

	when {
		isPtoF -> {
			println("Order #${old.orderNumber} is done")
			NotificationService.showNotification(
				"Order Updated",
				"Order #${old.orderNumber} is done"
			)
		}

		isPtoC -> {
			println("Order #${old.orderNumber} is canceled")
			NotificationService.showNotification(
				"Order Updated",
				"Order #${old.orderNumber} is canceled"
			)
		}

		isFCtoP -> println("Order #${new.orderNumber} is back to preparing")
		else -> {
			println("old data: $old")
			println("Updated data: $new")
		}
	}
}
