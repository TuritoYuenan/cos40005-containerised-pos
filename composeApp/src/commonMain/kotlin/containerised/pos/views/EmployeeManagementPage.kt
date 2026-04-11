package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.ErrorView
import containerised.pos.models.User.Companion.getStatusFromShift
import containerised.pos.models.User.Companion.updateLastLogin
import containerised.pos.models.User.Companion.updateLastLogout
import containerised.pos.models.User.EmployeeStatus
import containerised.pos.models.UserRole
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch

private const val CURRENT_BRANCH = "BRA26032801"

@Composable
fun EmployeeManagementPage(navController: NavController) {
	var userRoles by remember { mutableStateOf<List<UserRole>>(emptyList()) }
	var searchQuery by remember { mutableStateOf("") }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			userRoles = UserRole.fetchAndJoinByBranch(CURRENT_BRANCH)
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}
	val filteredUserRoles = remember(userRoles, searchQuery) {
		if (searchQuery.isBlank()) {
			userRoles
		} else {
			userRoles.filter {
				val matchesName = it.user?.fullName?.contains(searchQuery, true)
				val matchesRole = it.role.roleName.contains(searchQuery, true)
				matchesName?.or(matchesRole) ?: false
			}
		}
	}

	Column(Modifier.fillMaxSize()) {
		// Search bar
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth().padding(16.dp),
			placeholder = { Text("Search items by name or role...") },
			leadingIcon = { Icon(Icons.Filled.Search, "Search") },
			singleLine = true
		)

		if (error != null) return ErrorView(
			error ?: "Unknown error",
			Modifier.fillMaxSize()
		)

		LazyColumn {
			items(filteredUserRoles) { userRole ->
				userRole.EmployeeCard(navController)
			}
		}
	}
}

@Composable
fun UserRole.EmployeeCard(navController: NavController) {
	Card(
		Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.onPrimary)
			.clip(RoundedCornerShape(8.dp))
			.padding(12.dp, 6.dp)
			.clickable { navController.navigate(StaffRoutes.EmployeeProfile(userId)) },
		elevation = CardDefaults.cardElevation(
			defaultElevation = 12.dp
		)
	) {
		Row(
			Modifier
				.fillMaxWidth()
				.height(69.dp)
				.padding(12.dp, 6.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column() {
				Text(user?.fullName ?: "", style = MaterialTheme.typography.titleMedium)
				Text(role.roleName, style = MaterialTheme.typography.bodyMedium)
			}
			StatusBox()
		}
	}
}

@Composable
fun UserRole.StatusBox() {
	val scope = rememberCoroutineScope()
	var expanded by remember { mutableStateOf(false) }
	var status by remember {
		mutableStateOf(getStatusFromShift(user?.shift ?: emptyMap()))
	}

	Box(Modifier.clickable { expanded = !expanded }) {
		Text(
			status.toString(),
			Modifier
				.background(getStatusColor(status), RoundedCornerShape(8.dp))
				.padding(8.dp),
			style = MaterialTheme.typography.titleMedium,
		)

		if (status == EmployeeStatus.ACTIVE || status == EmployeeStatus.INACTIVE) {
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				if (status == EmployeeStatus.INACTIVE) {
					DropdownMenuItem(
						text = { Text(EmployeeStatus.ACTIVE.toString()) },
						onClick = {
							status = EmployeeStatus.ACTIVE
							expanded = false
							scope.launch { updateLastLogin(userId) }
						}
					)
				} else if (status == EmployeeStatus.ACTIVE) {
					DropdownMenuItem(
						text = { Text(EmployeeStatus.INACTIVE.toString()) },
						onClick = {
							status = EmployeeStatus.INACTIVE
							expanded = false
							scope.launch { updateLastLogout(userId) }
						}
					)
				}
			}
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
