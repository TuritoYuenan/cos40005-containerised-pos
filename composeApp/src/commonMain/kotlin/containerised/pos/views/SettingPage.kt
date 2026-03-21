package containerised.pos.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import containerised.pos.database.SupabaseClient
import containerised.pos.models.User
import kotlinx.coroutines.launch

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

	Column {
		Text(
			"Name: ${user?.fullName}",
			color = Color.Black,
			style = MaterialTheme.typography.titleMedium,
		)

		Text(
			"Email: $email",
			color = Color.Black,
			style = MaterialTheme.typography.titleMedium,
		)

		Row {
			Button(
				onClick = { scope.launch { logout() } },
				modifier = Modifier.height(40.dp),
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.outlineVariant,
					contentColor = Color.Black
				)
			) {
				Icon(Icons.AutoMirrored.Filled.Logout, "Logout")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Logout", color = Color.Black)
			}
		}
	}
}
