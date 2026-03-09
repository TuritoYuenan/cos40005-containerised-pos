package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.StockAdjustment
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private val defaultPadding = 16.dp

@Composable
fun StockHistoryPage(args: StaffRoutes.StockHistory) {
	val scope = rememberCoroutineScope()
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var records by remember { mutableStateOf<List<StockAdjustment>>(emptyList()) }

	suspend fun refreshRecords() {
		try {
			isLoading = true
			records = StockAdjustment
				.fetchByIngredientWithDetails(args.ingredientId)
				.sortedByDescending { it.createdAt }
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) { scope.launch { refreshRecords() } }

	Column(Modifier.fillMaxSize(), Arrangement.spacedBy(defaultPadding)) {
		if (isLoading) {
			LoadingView(Modifier.fillMaxSize())
			return
		}

		if (error != null) {
			ErrorView(
				"Failed to load stock adjustments\n$error",
				Modifier.fillMaxSize().padding(defaultPadding)
			)
			return
		}

		if (records.isEmpty()) {
			Column(
				Modifier.fillMaxSize().padding(defaultPadding),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("No stock adjustments found for this ingredient")
			}
			return
		}

		records.forEach { RecordCard(it) }
	}
}

@Composable
private fun RecordCard(record: StockAdjustment) {
	Card {
		Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
			AdjustmentTypeIcon(record.adjustmentType)

			Column(Modifier.padding(vertical = defaultPadding)) {
				val unit = record.ingredient?.unit ?: "units"
				Text(
					"${record.quantityBefore} \u2192 ${record.quantityAfter} $unit",
					style = MaterialTheme.typography.titleLarge
				)
				Text(
					record.createdAt.formatDate(),
					style = MaterialTheme.typography.bodyMedium
				)
				Text(
					if (record.notes != null) "\"${record.notes}\"" else "No notes given",
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}

@Composable
private fun AdjustmentTypeIcon(adjustmentType: StockAdjustment.Type) {
	Box(
		Modifier
			.padding(defaultPadding)
			.clip(CircleShape)
			.background(MaterialTheme.colorScheme.primaryContainer)
	) {
		val (icon, desc) = when (adjustmentType) {
			StockAdjustment.Type.DIRECT -> Icons.Default.Edit to "Direct Adjustment"
			StockAdjustment.Type.INCREMENT -> Icons.AutoMirrored.Filled.TrendingUp to "Increment"
			StockAdjustment.Type.DECREMENT -> Icons.AutoMirrored.Filled.TrendingDown to "Decrement"
		}
		Icon(
			icon,
			desc,
			Modifier.padding(8.dp),
			MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

private fun String.formatDate(): String {
	return try {
		val datetime = Instant.parse(this).toLocalDateTime(TimeZone.UTC)

		val (year, month, day) = listOf(
			datetime.year,
			datetime.month.number,
			datetime.day,
		).map { it.toString().padStart(2, '0') }

		val (hour, minute, second) = listOf(
			datetime.hour,
			datetime.minute,
			datetime.second,
		).map { it.toString().padStart(2, '0') }

		"On $year-$month-$day at $hour:$minute:$second"
	} catch (_: Exception) {
		this
	}
}

@Preview
@Composable
private fun RecordCardPreview() {
	RecordCard(StockAdjustment.MOCK)
}

@Preview(showBackground = true)
@Composable
private fun AdjustmentTypeIconPreview() {
	Row {
		AdjustmentTypeIcon(StockAdjustment.Type.DIRECT)
		AdjustmentTypeIcon(StockAdjustment.Type.INCREMENT)
		AdjustmentTypeIcon(StockAdjustment.Type.DECREMENT)
	}
}
