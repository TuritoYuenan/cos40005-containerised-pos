package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import containerised.pos.database.SupabaseClient.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

@Composable
fun LoginPage() {
	val scope = rememberCoroutineScope()
	val emailAddress = rememberTextFieldState("")
	var password by remember { mutableStateOf("") }
	var passwordVisible by remember { mutableStateOf(false) }

	Column(
		Modifier
			.fillMaxSize()
			.padding(defaultPadding)
			.verticalScroll(rememberScrollState()),
		Arrangement.spacedBy(16.dp),
		Alignment.CenterHorizontally,
	) {
		Box(
			Modifier
				.size(196.dp)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primary)
		) { }

		TextField(
			emailAddress,
			label = { Text("Email address") }
		)

		TextField(
			value = password,
			onValueChange = { password = it },
			label = { Text("Password") },
			singleLine = true,
			visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
			trailingIcon = {
				val image =
					if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

				// Localized description for accessibility services
				val description =
					if (passwordVisible) "Hide password" else "Show password"

				// Toggle button to hide or display password
				IconButton(onClick = { passwordVisible = !passwordVisible }) {
					Icon(imageVector = image, description)
				}
			}
		)

		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Button({ scope.launch { login(emailAddress.text.toString(), password) } }) {
				Icon(Icons.Filled.AccountCircle, "Login Icon")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Login")
			}

			TextButton({ /* TODO: Handle password reset */ }) {
				Text("Forget your password?")
			}
		}
	}
}

suspend fun login(email: String, password: String) = try {
	println("Email: '${email}'")
	println("Password length: ${password.length}")
	auth.signInWith(Email) {
		this.email = email
		this.password = password
	}
} catch (e: Exception) {
	println("Login failed: ${e.message}")
}
