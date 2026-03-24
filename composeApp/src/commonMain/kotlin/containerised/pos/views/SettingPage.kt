package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.database.SupabaseClient
import containerised.pos.models.User
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

private suspend fun logout() {
	try {
		SupabaseClient.auth.signOut()
	} catch (e: Exception) {
		println("Logout failed: ${e.message}")
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage() {
	val scope = rememberCoroutineScope()
	var user by remember { mutableStateOf<User?>(null) }
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	val email = SupabaseClient.auth.currentUserOrNull()?.email

	LaunchedEffect(Unit) {
		try {
			user = User.fetchById(userId ?: "")
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}

	Column(
		Modifier.padding(defaultPadding, 0.dp),
		Arrangement.spacedBy(defaultPadding)
	) {
		Text("User information", style = MaterialTheme.typography.headlineMedium)
		Text("Name: ${user?.fullName}", style = MaterialTheme.typography.titleMedium)
		Text("Email: $email", style = MaterialTheme.typography.titleMedium)

		Row {
			FilledTonalButton({ scope.launch { logout() } }) {
				Icon(Icons.AutoMirrored.Filled.Logout, "Logout")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Logout")
			}
		}
	}
}
