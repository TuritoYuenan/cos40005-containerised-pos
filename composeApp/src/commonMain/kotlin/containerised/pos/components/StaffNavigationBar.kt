package containerised.pos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tab
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
	NavigationItem("Sales", StaffRoutes.Login, Icons.Filled.Inbox),
	NavigationItem("Inventory", StaffRoutes.Inventory, Icons.Filled.Folder),
	NavigationItem("Employee", StaffRoutes.EmployeeManagement, Icons.Filled.Badge),
	NavigationItem("Setting", StaffRoutes.Setting, Icons.Filled.Settings)
)

@Composable
fun StaffNavigationBar(navController: NavController, userPermissions: List<String>) {
	var selectedDestination by remember { mutableStateOf(getStartRoute(userPermissions)) }

	val allowedNavItems = navItems.filter { item ->
		item.label == "Employee" || item.label == "Setting" || userPermissions.contains(item.label)
	}
	NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
		for (item in allowedNavItems) {
			NavigationBarItem(
				selected = selectedDestination == item.route,
				label = { Text(item.label) },
				icon = { Icon(item.icon, item.label) },
				onClick = {
					try {
						navController.navigate(item.route) {
							launchSingleTop = true
							restoreState = true
							popUpTo(navController.graph.startDestinationId) {
								saveState = true
							}
						}
						selectedDestination = item.route
					} catch (e: Exception) {
						println("Navigation to ${item.route} failed: ${e.message}")
					}
				}
			)
		}
	}
}

fun getStartRoute(permissions: List<String>): Any {
	val allowedNavItems = navItems.filter { it.label in permissions }
	return allowedNavItems.firstOrNull()?.route ?: StaffRoutes.Login
}
