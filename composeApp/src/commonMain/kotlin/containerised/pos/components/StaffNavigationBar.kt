package containerised.pos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import containerised.pos.routes.StaffRoutes

data class NavigationItem(val label: String, val route: Any, val icon: ImageVector)

val navItems = listOf(
	NavigationItem("Order", StaffRoutes.Inventory, Icons.AutoMirrored.Filled.Comment),
	NavigationItem("Menu", StaffRoutes.MenuEdit, Icons.AutoMirrored.Filled.MenuOpen),
	NavigationItem("Kitchen", StaffRoutes.KitchenDisplay, Icons.Filled.Tab),
	NavigationItem("Sales", StaffRoutes.Inventory, Icons.Filled.Inbox),
	NavigationItem("Inventory", StaffRoutes.Inventory, Icons.Filled.Folder)
)

@Composable
fun StaffNavigationBar(navController: NavController, startDestination: Any) {
	var selectedDestination by remember { mutableStateOf(startDestination) }

	NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
		for (item in navItems) {
			NavigationBarItem(
				selected = selectedDestination == item.route,
				label = { Text(item.label) },
				icon = {
					Icon(
						imageVector = item.icon,
						contentDescription = item.label
					)
				},
				onClick = {
					try {
						navController.navigate(item.route)
						selectedDestination = item.route
					} catch (e: Exception) {
						println("Navigation to ${item.route} failed: ${e.message}")
					}
				},
			)
		}
	}
}
