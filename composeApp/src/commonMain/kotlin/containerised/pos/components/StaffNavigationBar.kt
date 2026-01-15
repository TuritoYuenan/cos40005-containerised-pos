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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class NavigationItem(val label: String, val route: String, val icon: ImageVector)

val navItems = listOf(
	NavigationItem("Order", "order", Icons.AutoMirrored.Filled.Comment),
	NavigationItem("Menu", "menu_list", Icons.AutoMirrored.Filled.MenuOpen),
	NavigationItem("Kitchen", "kitchen", Icons.Filled.Tab),
	NavigationItem("Sales", "sales", Icons.Filled.Inbox),
	NavigationItem("Inventory", "inventory", Icons.Filled.Folder)
)

@Composable
fun StaffNavigationBar(navController: NavController) {
	var selectedDestination by rememberSaveable { mutableStateOf("login") }

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
