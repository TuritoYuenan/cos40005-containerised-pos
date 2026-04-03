package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.SettingsButton
import containerised.pos.database.SupabaseClient
import containerised.pos.models.EmployeeShift
import containerised.pos.models.EmployeeShift.Companion.isInShift
import containerised.pos.models.UserRole
import containerised.pos.models.days
import containerised.pos.models.hours
import containerised.pos.routes.StaffRoutes


@Composable
fun EmployeeProfilePage(navController: NavController) {
	val scope = rememberCoroutineScope()
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	var userRole by remember { mutableStateOf<UserRole?>(null) }
	var shifts by remember { mutableStateOf<List<EmployeeShift>>(emptyList()) }

	LaunchedEffect(Unit) {
		try {
			userRole = UserRole.fetchAndJoin(userId?: "")
			shifts = EmployeeShift.fetchById(userId?: "")
		} catch (e: Exception) {
			val error = e.message
			println("Error: $error")
		}
	}

	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(12.dp)
	){
		item {
			userRole?.EmployeeCard(navController)
		}
		item{
			userRole?.DetailCard()
		}
		item{
			shifts.Timetable()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRole.EmployeeCard(navController: NavController){
	TopAppBar(

		title = {
			Text(
				text = user?.fullName ?: "",
				style = MaterialTheme.typography.titleLarge,
			)
		},
		actions = {
			SettingsButton { navController.navigate(StaffRoutes.Setting) }
		}

	)
}

@Composable
fun UserRole.DetailCard(){
	OutlinedCard(
		Modifier
			.fillMaxWidth()
			.padding(12.dp, 6.dp),
		) {
		Row(
			Modifier
				.fillMaxWidth()
				.padding(12.dp, 6.dp),
		) {
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text("DEPARTMENT", style = MaterialTheme.typography.bodyMedium)
				Text(role.roleName, style = MaterialTheme.typography.titleMedium)
			}

			Column(
				modifier = Modifier.weight(1f)
			) {
				Text("STATUS", style = MaterialTheme.typography.bodyMedium)

				Row(verticalAlignment = Alignment.CenterVertically) {
					val isActive = user?.isActive == true
					Box(
						modifier = Modifier
							.size(10.dp)
							.background(
								color = if (isActive) Color(0xFF4CAF50) else Color(0xFFF44336),
								shape = CircleShape
							)
					)

					Spacer(modifier = Modifier.width(6.dp))

					Text(
						text = if (isActive) "Active" else "Inactive",
						style = MaterialTheme.typography.titleMedium
					)
				}
			}
		}
	}
	OutlinedCard(
		Modifier
			.fillMaxWidth()
			.padding(12.dp, 6.dp),
		) {
		Column(
			Modifier
				.fillMaxWidth()
				.padding(12.dp, 6.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Row() {
				Icon(Icons.Filled.Mail, "Mail")
				Column() {
					Text("EMAIL", style = MaterialTheme.typography.bodyMedium)
					Text(
						user?.email ?: "null",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
			if (user?.phone != null) {
				Row() {
					Icon(Icons.Filled.Phone, "Phone")
					Column() {
						Text("PHONE", style = MaterialTheme.typography.bodyMedium)
						Text(user.phone, style = MaterialTheme.typography.bodyMedium)
					}
				}
			}
			Row() {
				Icon(Icons.Filled.AccessTime, "clock")
				Column() {
					Text("JOINED", style = MaterialTheme.typography.bodyMedium)
					Text(
						user?.createdAt?.take(10) ?: "null",
						style = MaterialTheme.typography.bodyMedium
					)

				}
			}
		}
	}
}
@Composable
fun List<EmployeeShift>.Timetable() {
	val borderColor = MaterialTheme.colorScheme.outline
	val hourSpace = 40.dp
	Column(
		Modifier
			.fillMaxWidth()
			.padding(12.dp, 6.dp)
			.border(1.dp, borderColor)
			.drawBehind {
				val hourWidthPx = hourSpace.toPx()
				val remainingWidth = size.width - hourWidthPx
				val dayColumnWidth = remainingWidth / days.size

				repeat(days.size) { index ->
					val x = hourWidthPx + (dayColumnWidth * index)

					drawLine(
						color = borderColor,
						start = Offset(x, 0f),
						end = Offset(x, size.height),
						strokeWidth = 1.dp.toPx()
					)
				}
			}
	) {
		// Header row (days)
		Row {
			Spacer(modifier = Modifier.width(hourSpace)) // space for hour labels

			days.forEach { day ->
				Box(
					modifier = Modifier
						.weight(1f)
						.padding(2.dp),
					contentAlignment = Alignment.Center
				) {
					Text(day, style = MaterialTheme.typography.bodyMedium)
				}
			}
		}

		// Time rows
		hours.forEachIndexed { index, hour ->
			Row {
				// Hour label
				Box(
					modifier = Modifier
						.width(hourSpace)
						.padding(4.dp),
					contentAlignment = Alignment.CenterStart
				) {
					Text(hour, style = MaterialTheme.typography.bodySmall)
				}

				// Cells
				days.forEach { day ->
					val hasShift = any { shift ->
						shift.date.toString() == day && isInShift(index, shift)
					}

					Box(
						modifier = Modifier
							.weight(1f)
							.height(24.dp)
							.padding(2.dp)
							.background(
								if (hasShift) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
								else MaterialTheme.colorScheme.surfaceVariant
							)
					)
				}
			}
		}
	}
}
