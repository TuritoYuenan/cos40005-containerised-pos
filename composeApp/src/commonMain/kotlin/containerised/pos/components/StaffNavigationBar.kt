package containerised.pos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import containerised.pos.routes.StaffRoutes

data class NavigationItem(val label: String, val route: Any, val icon: ImageVector)

val navItems = listOf(
	NavigationItem("Order", StaffRoutes.Login, Icons.AutoMirrored.Filled.Comment),
	NavigationItem("Menu", StaffRoutes.MenuEdit, Icons.AutoMirrored.Filled.MenuOpen),
	NavigationItem("Kitchen", StaffRoutes.KitchenDisplay, Icons.Filled.Tab),
	NavigationItem("Sales", StaffRoutes.Login, Icons.Filled.Inbox),
	NavigationItem("Inventory", StaffRoutes.Inventory, Icons.Filled.Folder),
	NavigationItem("Setting", StaffRoutes.Setting, Icons.Filled.Settings)
)

@Composable
fun StaffNavigationBar(navController: NavController, userPermissions: List<String>) {
	val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

	val allowedNavItems = navItems.filter { item ->
		item.label == "Setting" || userPermissions.contains(item.label)
	}
	NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
		for (item in allowedNavItems) {
			NavigationBarItem(
				selected = currentRoute == item.route,
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
