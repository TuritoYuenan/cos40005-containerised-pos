package containerised.pos.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.database.SupabaseClient
import kotlinx.coroutines.launch

suspend fun logout() {
	SupabaseClient.auth.signOut()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(navController: NavController) {
	val scope = rememberCoroutineScope()
	Column{
		Row {
			Button(
				onClick = {
					scope.launch {
						try {
							logout()
						} catch (e: Exception) {
							println("Logout failed: ${e.message}")
						}
					}
				},
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.outlineVariant,
					contentColor = Color.Black
				),
				modifier = Modifier.height(40.dp)
			) {
				Icon(
					Icons.Filled.Logout,
					contentDescription = "Logout"
				)
				Text("Logout", color = Color.Black)
			}
		}
	}
}
