package containerised.pos.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import containerised.pos.database.SupabaseClient
import containerised.pos.models.UserRole

private val defaultPadding = 16.dp

@Composable
fun EmployeeManagementPage() {
	val scope = rememberCoroutineScope()
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	var userRole by remember { mutableStateOf<UserRole?>(null) }

	LaunchedEffect(Unit) {
		try {
			userRole = UserRole.fetchAndJoin(userId?: "")
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}

	Column(
		Modifier.padding(defaultPadding, 0.dp),
		Arrangement.spacedBy(defaultPadding)
	) {
		userRole?.EmployeeCard()
		userRole?.DetailCard()
	}
}

@Composable
fun UserRole.EmployeeCard(){
	Card(Modifier.fillMaxWidth(),) {
		Column() {
			Text("${user?.fullName}", style = MaterialTheme.typography.titleLarge)
		}
	}
}

@Composable
fun UserRole.DetailCard(){
	Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp),) {
		Row() {
			Column() {
				Text("DEPARTMENT", style = MaterialTheme.typography.bodyMedium)
				Text(role.roleName, style = MaterialTheme.typography.titleMedium)
			}
			Column() {
				Text("STATUS", style = MaterialTheme.typography.bodyMedium)
				if (user?.isActive == true) {
					Text("Active", style = MaterialTheme.typography.titleMedium)
				} else {
					Text("Inactive", style = MaterialTheme.typography.titleMedium)
				}
			}
		}
	}
	Card(Modifier.fillMaxWidth().padding(12.dp, 6.dp),) {
		Column() {
			Text("EMAIL", style = MaterialTheme.typography.bodyMedium)
			Text(user?.email ?: "null", style = MaterialTheme.typography.bodyMedium)
			if (user?.phone != null) {
				Text("PHONE", style = MaterialTheme.typography.bodyMedium)
				Text(user.phone, style = MaterialTheme.typography.bodyMedium)
			}
			Text("JOINED", style = MaterialTheme.typography.bodyMedium)
			Text(user?.createdAt ?: "null", style = MaterialTheme.typography.bodyMedium)
		}
	}
}
