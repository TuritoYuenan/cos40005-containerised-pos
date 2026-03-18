package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import containerised.pos.RealtimeManager
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.models.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord

private const val CURRENT_BRANCH = "BRA26011700"
private val defaultPadding = 16.dp

@Composable
fun OrderConfirmPage() {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(false) }
	var orders by remember { mutableStateOf(emptyList<Order>()) }

	LaunchedEffect(Unit) {
		try {
			isLoading = true
			orders = Order.fetchByBranch(CURRENT_BRANCH)
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) {
		RealtimeManager.forOrders.start()
		RealtimeManager.forOrders.events.collect { action ->
			when (action) {
				is PostgresAction.Insert -> orders = orders.onChange(action)
				is PostgresAction.Update -> { /* TODO: Implement live order update */}
				is PostgresAction.Delete -> orders = orders.onChange(action)
				is PostgresAction.Select -> {}
			}
		}
	}

	Column(Modifier.fillMaxSize().padding(defaultPadding)) {
		if (isLoading) return LoadingView(Modifier.fillMaxSize())
		if (error != null) return ErrorView(
			error ?: "Unknown error",
			Modifier.fillMaxSize(),
		)

		LazyColumn(verticalArrangement = Arrangement.spacedBy(defaultPadding)) {
			items(orders.size) { orders[it].Card(Modifier.fillMaxWidth()) }
		}
	}
}

@Composable
private fun Order.Card(modifier: Modifier = Modifier) {
	OutlinedCard(modifier) {
		Column(
			Modifier.padding(defaultPadding),
			Arrangement.spacedBy(defaultPadding),
		) {
			Row(Modifier.fillMaxWidth()) {
				Column {
					Text("Order #$orderNumber", style = MaterialTheme.typography.headlineMedium)

					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(Icons.Default.TableRestaurant, "Table")
						Spacer(Modifier.size(ButtonDefaults.IconSpacing))
						Text("Table: ${table?.tableCode}")
					}

					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(Icons.Default.Info, "Status")
						Spacer(Modifier.size(ButtonDefaults.IconSpacing))
						Text("Status: $status")
					}
				}
			}

			HorizontalDivider()
			ActionButtons(
				onCancel = { /* TODO: Implement cancel logic */ },
				onPrepare = { /* TODO: Implement prepare logic */ }
			)
		}
	}
}

@Composable
private fun ActionButtons(
	modifier: Modifier = Modifier,
	onCancel: () -> Unit = {},
	onPrepare: () -> Unit = {},
) = Row(
	modifier.fillMaxWidth(),
	Arrangement.spacedBy(defaultPadding, Alignment.End),
) {
	Button(onCancel) { Text("Cancel") }
	Button(onPrepare) { Text("Prepare") }
}

private fun List<Order>.onChange(action: PostgresAction.Insert): List<Order> {
	return this + action.decodeRecord<Order>()
}

private fun List<Order>.onChange(action: PostgresAction.Delete): List<Order> {
	return this.filterNot { it.orderId == action.decodeOldRecord<Order>().orderId }
}

@Preview
@Composable
fun OrderCardPreview() = Order.MOCK.Card(Modifier.fillMaxWidth())
