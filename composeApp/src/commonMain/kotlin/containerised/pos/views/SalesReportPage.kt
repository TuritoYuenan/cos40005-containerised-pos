package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.database.SupabaseClient
import containerised.pos.models.SalesReport
import containerised.pos.routes.StaffRoutes
import containerised.pos.services.downloadService
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

@Composable
fun SalesReportPage(args: StaffRoutes.SalesReport) {
	val scope = rememberCoroutineScope()
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var isDownloading by remember { mutableStateOf(false) }
	var downloadError by remember { mutableStateOf<String?>(null) }
	var salesReport by remember { mutableStateOf<SalesReport?>(null) }

	/**
	 * Call the `export-sales-report` Supabase Edge Function
	 * POST a JSON payload with report_id
	 * Receive a public URL to download
	 */
	suspend fun onDownloadReport() {
		val reportId = salesReport?.id ?: return

		try {
			isDownloading = true
			downloadError = null

			val response = SupabaseClient.exportSalesReport(reportId)
			downloadService.download(response.url)
		} catch (e: Exception) {
			downloadError = e.message ?: "Failed to download report"
		} finally {
			isDownloading = false
		}
	}

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

		Button(
			onClick = { scope.launch { onDownloadReport() } },
			enabled = !isDownloading
		) {
			if (isDownloading) {
				CircularProgressIndicator(
					modifier = Modifier.size(ButtonDefaults.IconSize),
					strokeWidth = 2.dp
				)
			} else {
				Icon(Icons.Default.Download, "Download")
			}
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Text(if (isDownloading) "Preparing..." else "Download Report")
		}

		downloadError?.let {
			Text(it, color = MaterialTheme.colorScheme.error)
		}
	}
}
