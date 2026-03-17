package containerised.pos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import containerised.pos.components.StaffNavigationBar
import containerised.pos.components.StaffTopBar
import containerised.pos.components.getStartRoute
import containerised.pos.database.SupabaseClient
import containerised.pos.models.Order
import containerised.pos.models.OrderStatus
import containerised.pos.models.UserRole.Companion.fetchUserPermission
import containerised.pos.routes.CustomerRoutes
import containerised.pos.routes.StaffRoutes
import containerised.pos.views.*
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppNavHost(onNavHostReady: suspend (NavController) -> Unit = {}) {
	val navController = rememberNavController()

//	Must change "order" to "login" when auth is implemented
//	val startDestination = if (isWeb) CustomerRoutes.Order("Unknown", "Unknown") else if (session == null) StaffRoutes.Login else getStartRoute(userPermissions)

	MaterialTheme {
//		Staff-facing application, available on mobile and desktop
		if (!isWeb) {
			var userPermissions by remember { mutableStateOf<List<String>>(emptyList()) }
			val session = SupabaseClient.auth.currentSessionOrNull()

			val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
			val isLogin = currentRoute == StaffRoutes.Login::class.qualifiedName

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
							}
							else if (old.status == OrderStatus.PREPARING && updated.status == OrderStatus.CANCELED) {
								println("deleted data: $old")
								NotificationService.showNotification(
									title = "Order Updated",
									message = "Order #${old.orderNumber} is canceled"
								)
							}
							else if ((old.status == OrderStatus.FINISHED || old.status == OrderStatus.CANCELED) && updated.status == OrderStatus.PREPARING){
								println("Insert data: $updated")
							}
							else{
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

			LaunchedEffect(Unit) {
				SupabaseClient.auth.sessionStatus
					.collectLatest { status ->
						when (status) {
							is SessionStatus.Authenticated -> {
								val userId = SupabaseClient.auth.currentUserOrNull()?.id
								if (userId != null) {
									val permissions = fetchUserPermission(userId)
									userPermissions = permissions
									val route = getStartRoute(permissions)
									navController.navigate(route) {
										popUpTo(0) { inclusive = true }
										launchSingleTop = true
									}
								}
							}

							is SessionStatus.NotAuthenticated -> {
								navController.navigate(StaffRoutes.Login) {
									popUpTo(0) { inclusive = true }
									launchSingleTop = true
								}
							}

							else -> Unit
						}
					}
			}
			Scaffold(
				topBar = {
					if (!isLogin) {
						StaffTopBar(navController, currentRoute)
					}
				},
				bottomBar = {
					if (!isLogin) {
						StaffNavigationBar(navController, userPermissions)
					}
				}
			) { paddingValues ->
				NavHost(
					navController = navController,
					startDestination = StaffRoutes.Login,
					modifier = Modifier.padding(paddingValues)
				) {
					composable<StaffRoutes.Login> { LoginPage(navController) }
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
					composable<StaffRoutes.Setting>{SettingPage(navController)}
				}
			}
		}

//		Customer-facing application, available on web only
		if (isWeb) {
			val startDestination = CustomerRoutes.Order("Unknown", "Unknown")
			NavHost(
				navController = navController,
				startDestination = startDestination,
			) {
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
	}

	LaunchedEffect(navController) { onNavHostReady(navController) }
}
