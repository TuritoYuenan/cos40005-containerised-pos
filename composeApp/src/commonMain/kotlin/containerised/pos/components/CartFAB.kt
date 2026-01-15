package containerised.pos.components

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CartFAB(navController: NavController) {
	ExtendedFloatingActionButton(
		onClick = { navController.navigate("checkout") },
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = MaterialTheme.colorScheme.onPrimary,
	) {
		Text("View Cart (3 items) - $29.97")
	}
}
