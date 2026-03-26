package containerised.pos.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.database.SupabaseClient
import containerised.pos.models.User

private val defaultPadding = 16.dp

@Composable
fun EmployeeManagementPage() {
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

		}
	}
}
