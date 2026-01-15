package containerised.pos.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.baseline_menu_24

data class NavigationItem(val label: String, val route: String, val icon: DrawableResource)

val navItems = listOf(
	NavigationItem("Order", "order", Res.drawable.baseline_menu_24),
	NavigationItem("Menu", "menu_list", Res.drawable.baseline_menu_24),
	NavigationItem("Kitchen", "kitchen", Res.drawable.baseline_menu_24),
	NavigationItem("Sales", "sales", Res.drawable.baseline_menu_24),
	NavigationItem("Inventory", "inventory", Res.drawable.baseline_menu_24)
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
						imageVector = vectorResource(item.icon),
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
