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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun LoginPage() {
	val padding = 16.dp
	val emailAddress = rememberTextFieldState("")
	var password by remember { mutableStateOf("") }
	var passwordVisible by remember { mutableStateOf(false) }

	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.verticalScroll(rememberScrollState()),
	) {
		Box(
			modifier = Modifier
				.size(196.dp)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primary)
		) { }

		TextField(
			state = emailAddress,
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
				val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

				// Localized description for accessibility services
				val description = if (passwordVisible) "Hide password" else "Show password"

				// Toggle button to hide or display password
				IconButton(onClick = { passwordVisible = !passwordVisible }) {
					Icon(imageVector = image, description)
				}
			}
		)

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Button(onClick = { /* Handle login action */ }) {
				Icon(imageVector = Icons.Filled.AccountCircle, "Login Icon")
				Text("Login")
			}

			TextButton(onClick = { /* Handle login action */ }) {
				Text("Forget your password?")
			}
		}
	}
}
