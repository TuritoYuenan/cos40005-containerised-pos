package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.routes.StaffRoutes

private val defaultPadding = 16.dp

@Composable
fun SalesPage(navController: NavController) {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var searchQuery by remember { mutableStateOf("") }

	Column(Modifier.fillMaxSize()) {
		// Search bar
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth().padding(defaultPadding),
			placeholder = { Text("Search sales reports...") },
			leadingIcon = { Icon(Icons.Filled.Search, "Search") },
			singleLine = true
		)

		if (isLoading) return LoadingView(Modifier.fillMaxSize())

		if (error != null) return ErrorView(
			error ?: "Unknown error",
			Modifier.fillMaxSize()
		)

		LazyColumn(
			Modifier.padding(horizontal = defaultPadding),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(10) { i ->
				Button({
					navController.navigate(StaffRoutes.SalesReport(i.toString()))
				}) {
					Text("Sales Report #$i")
				}
			}
		}
	}
}
