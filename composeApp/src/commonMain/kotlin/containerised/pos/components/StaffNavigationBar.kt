package containerised.pos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import containerised.pos.routes.StaffRoutes

data class NavigationItem(val label: String, val route: Any, val icon: ImageVector)

val navItems = listOf(
	NavigationItem("Order", StaffRoutes.OrderConfirm, Icons.AutoMirrored.Filled.Comment),
	NavigationItem("Menu", StaffRoutes.MenuEdit, Icons.AutoMirrored.Filled.MenuOpen),
	NavigationItem("Kitchen", StaffRoutes.KitchenDisplay, Icons.Filled.Tab),
	NavigationItem("Sales", StaffRoutes.Sales, Icons.Filled.Money),
	NavigationItem("Inventory", StaffRoutes.Inventory, Icons.Filled.Folder),
	NavigationItem("Employee", StaffRoutes.EmployeeManagement, Icons.Filled.Badge),
)

@Composable
fun StaffNavigationBar(navController: NavController, userPermissions: List<String>) {
	var selectedDestination by remember { mutableStateOf(getStartRoute(userPermissions)) }
	val allowedNavItems = navItems.filter { item ->
		item.label == "Employee" ||  userPermissions.contains(item.label)
	}

	fun NavigationItem.goTo() {
		try {
			navController.navigate(route) {
				launchSingleTop = true
				restoreState = true
				popUpTo(navController.graph.startDestinationId) {
					saveState = true
				}
			}
			selectedDestination = route
		} catch (e: Exception) {
			println("Navigation to $route failed: ${e.message}")
		}
	}

	NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
		allowedNavItems.forEach {
			NavigationBarItem(
				selected = selectedDestination == it.route,
				onClick = { it.goTo() },
				icon = { Icon(it.icon, it.label) },
				label = { Text(it.label) },
			)
		}
	}
}

fun getStartRoute(permissions: List<String>): Any {
	val allowedNavItems = navItems.filter { it.label in permissions }
	return allowedNavItems.firstOrNull()?.route ?: StaffRoutes.Login
}
