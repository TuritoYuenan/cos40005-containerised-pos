package containerised.pos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.AppTheme
import containerised.pos.components.LoadingView
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.components.getStartRoute
import containerised.pos.database.SupabaseClient
import containerised.pos.models.Order
import containerised.pos.models.UserRole.Companion.fetchUserPermission
import containerised.pos.routes.StaffRoutes
import containerised.pos.services.RealtimeManager
import containerised.pos.services.notificationService
import containerised.pos.services.realtimeServiceController
import containerised.pos.views.*
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost() {
	val navController = rememberNavController()

	AppTheme {
		var userPermissions by remember { mutableStateOf<List<String>>(emptyList()) }
		SupabaseClient.auth.currentSessionOrNull()

		val currentRoute = navController
			.currentBackStackEntryAsState()
			.value?.destination?.route.orEmpty()

		val isLogin = currentRoute == "login"

		LaunchedEffect(Unit) {
			realtimeServiceController.start()
		}

		LaunchedEffect(userPermissions) {
			if (userPermissions.contains("Kitchen")) {
				RealtimeManager.forOrders.events.collect { action ->
					when (action) {
						is PostgresAction.Insert -> action.handle()
						is PostgresAction.Update -> action.handle()
						is PostgresAction.Delete -> action.handle()
						is PostgresAction.Select -> Unit
					}
				}
			}
		}

		LaunchedEffect(navController) {
			// Wait until NavHost has attached a graph/start destination
			navController.currentBackStackEntryFlow.first()

			SupabaseClient.auth.sessionStatus.collectLatest { status ->
				when (status) {
					is SessionStatus.Authenticated -> {
						val userId = SupabaseClient.auth.currentUserOrNull()?.id
							?: return@collectLatest

						userPermissions = fetchUserPermission(userId)
						navController.navigate(getStartRoute(userPermissions)) {
							popUpTo(navController.graph.id) { inclusive = true }
							launchSingleTop = true
						}
					}

					is SessionStatus.NotAuthenticated -> {
						navController.navigate(StaffRoutes.Login) {
							popUpTo(navController.graph.id) { inclusive = true }
							launchSingleTop = true
						}
					}

					else -> Unit
				}
			}
		}

		Scaffold(
			topBar = { if (!isLogin) StaffTopBar(navController, currentRoute) },
			bottomBar = {
				if (!isLogin) StaffNavigationBar(navController, userPermissions)
			}
		) { paddingValues ->
			NavHost(
				navController,
				StaffRoutes.Loading,
				Modifier.padding(paddingValues)
			) {
				composable<StaffRoutes.Loading> { LoadingView(Modifier.fillMaxSize()) }
				composable<StaffRoutes.Login> { LoginPage() }
				composable<StaffRoutes.MenuEdit> { MenuEditPage(navController) }
				composable<StaffRoutes.EditItem> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.EditItem>()
					EditItemPage(navController, args.itemId)
				}
				composable<StaffRoutes.EditTag> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.EditTag>()
					EditTagPage(navController, args.tagId)
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
				composable<StaffRoutes.Sales> { SalesPage(navController) }
				composable<StaffRoutes.SalesReport> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.SalesReport>()
					SalesReportPage(args)
				}
				composable<StaffRoutes.EmployeeProfile> { backStackEntry ->
					val args = backStackEntry.toRoute<StaffRoutes.EmployeeProfile>()
					EmployeeProfilePage(navController, args.argUserId)
				}
				composable<StaffRoutes.EmployeeManagement> { EmployeeManagementPage(navController) }
				composable<StaffRoutes.Setting> { SettingPage() }
			}
		}
	}
}

private fun PostgresAction.Insert.handle() {
	val new = this.decodeRecord<Order>()
	println("Insert data: $new")

	notificationService.showNotification(
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
			notificationService.showNotification(
				"Order Updated",
				"Order #${old.orderNumber} is done"
			)
		}

		isPtoC -> {
			println("Order #${old.orderNumber} is canceled")
			notificationService.showNotification(
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
