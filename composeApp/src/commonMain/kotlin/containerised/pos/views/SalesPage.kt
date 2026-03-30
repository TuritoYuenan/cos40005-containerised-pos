package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.SalesReport
import containerised.pos.routes.StaffRoutes

private val defaultPadding = 16.dp

@Composable
fun SalesPage(navController: NavController) {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var salesReports by remember { mutableStateOf<List<SalesReport>>(emptyList()) }
	var searchQuery by remember { mutableStateOf("") }

	LaunchedEffect(Unit) {
		try {
			isLoading = true
			salesReports = SalesReport.fetchAll().sortedByDescending { it.periodEnd }
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

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
			items(salesReports.size) { i ->
				salesReports[i].Card(navController)
			}
		}
	}
}

@Composable
fun SalesReport.Card(navController: NavController?) = OutlinedCard(
	{ navController?.navigate(StaffRoutes.SalesReport(id)) }
) {
	Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
		Box(
			Modifier
				.padding(defaultPadding)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primaryContainer)
		) {
			Icon(
				Icons.Filled.Search,
				"View Report",
				Modifier.padding(8.dp),
				MaterialTheme.colorScheme.onPrimaryContainer
			)
		}

		Spacer(Modifier.size(ButtonDefaults.IconSpacing))

		Column(
			Modifier.padding(vertical = defaultPadding),
			Arrangement.spacedBy(8.dp)
		) {
			Text(periodEnd.toString(), style = MaterialTheme.typography.headlineMedium)
			Text(id, style = MaterialTheme.typography.bodyMedium)
		}
	}
}

@Preview
@Composable
fun ReportCardPreview() = SalesReport.MOCK.Card(null)
