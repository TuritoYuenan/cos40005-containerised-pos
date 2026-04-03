package containerised.pos.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import containerised.pos.models.User
import containerised.pos.models.UserRole

private const val CURRENT_BRANCH = "BRA26011700"
@Composable
fun EmployeeManagementPage(navController: NavController) {
	var userRoles by remember { mutableStateOf<List<UserRole>>(emptyList()) }

	LaunchedEffect(Unit) {
		try {
			userRoles = UserRole.fetchAndJoinByBranch(CURRENT_BRANCH)
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}

	LazyColumn {
		items(userRoles) { userRole ->
			userRole.EmployeeCard()
		}
	}
}

@Composable
fun UserRole.EmployeeCard(){
	Card(){
		Row(){
			Column(){
				Text(user?.fullName ?: "", style = MaterialTheme.typography.bodyMedium)
				Text(role.roleName, style = MaterialTheme.typography.titleMedium)
			}
			Text(user?.employeeStatus?.name ?: "", style = MaterialTheme.typography.bodyMedium)
		}
	}
}
