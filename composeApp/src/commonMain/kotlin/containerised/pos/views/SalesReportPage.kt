package containerised.pos.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.SalesReport
import containerised.pos.routes.StaffRoutes

private val defaultPadding = 16.dp

@Composable
fun SalesReportPage(args: StaffRoutes.SalesReport) {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var salesReport by remember { mutableStateOf<SalesReport?>(null) }

	LaunchedEffect(args.id) {
		try {
			isLoading = true
			salesReport = SalesReport.fetchByID(args.id)
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	Column(
		Modifier
			.fillMaxSize()
			.padding(defaultPadding)
			.verticalScroll(rememberScrollState()),
		Arrangement.spacedBy(defaultPadding)
	) {
		if (isLoading) return LoadingView(Modifier.fillMaxSize())

		if (error != null) return ErrorView(
			error ?: "Unknown error",
			Modifier.fillMaxSize()
		)

		if (salesReport == null) return ErrorView(
			"Sales report not found",
			Modifier.fillMaxSize()
		)

		Text("Sales Report", style = MaterialTheme.typography.headlineMedium)
		Text("Period: ${salesReport!!.periodStart} to ${salesReport!!.periodEnd}")
		Text("Granularity: ${salesReport!!.granularity}")
		Text("Gross Sales: ${salesReport!!.grossSales} ${salesReport!!.currency}")
		Text("Discount Total: ${salesReport!!.discountTotal} ${salesReport!!.currency}")
		Text("Net Sales: ${salesReport!!.netSales} ${salesReport!!.currency}")
		Text("Orders Count: ${salesReport!!.ordersCount}")
	}
}
