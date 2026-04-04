package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.models.User
import containerised.pos.models.User.EmployeeStatus
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
	Card(
		Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.onPrimary)
			.clip(RoundedCornerShape(8.dp))
			.padding(12.dp, 6.dp),
		elevation = CardDefaults.cardElevation(
			defaultElevation = 16.dp
		)
	){
		Row(
			Modifier
				.fillMaxWidth()
				.height(69.dp)
				.padding(12.dp, 6.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		){
			Column(){
				Text(user?.fullName ?: "", style = MaterialTheme.typography.titleMedium)
				Text(role.roleName, style = MaterialTheme.typography.bodyMedium)
			}
			Text(
				text = user?.employeeStatus?.name ?: "",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier
					.background(getStatusColor(user?.employeeStatus), RoundedCornerShape(8.dp))
					.padding(8.dp)
			)
		}
	}
}
@Composable
fun getStatusColor(status: EmployeeStatus?): Color {
	return when (status) {
		EmployeeStatus.ACTIVE -> MaterialTheme.colorScheme.primary
		EmployeeStatus.INACTIVE -> MaterialTheme.colorScheme.error
		EmployeeStatus.BREAK -> Color(0xFFFFC107)
		EmployeeStatus.OFF_DUTY -> Color.LightGray
		else -> Color.Transparent
	}
}
