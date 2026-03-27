package containerised.pos.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.routes.StaffRoutes

private val defaultPadding = 16.dp

@Composable
fun SalesReportPage(navController: NavController, args: StaffRoutes.SalesReport) {
	Column(
		Modifier
			.fillMaxSize()
			.padding(defaultPadding)
			.verticalScroll(rememberScrollState()),
		Arrangement.spacedBy(defaultPadding)
	) {
		// TODO: Implement actual sales report content
	}
}
