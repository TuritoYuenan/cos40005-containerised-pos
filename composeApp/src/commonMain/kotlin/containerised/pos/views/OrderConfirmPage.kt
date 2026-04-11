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
import containerised.pos.services.RealtimeManager
import containerised.pos.components.ErrorView
import containerised.pos.components.LoadingView
import containerised.pos.database.SupabaseClient
import containerised.pos.models.Order
import containerised.pos.models.User.Companion.fetchBranchById
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

@Composable
fun OrderConfirmPage() {
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(false) }
	var orders by remember { mutableStateOf(emptyList<Order>()) }
	val userId = SupabaseClient.auth.currentUserOrNull()?.id
	var branchId by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			branchId = fetchBranchById(userId?: "")
			isLoading = true
			orders = Order.fetchByBranch(branchId!!)
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) {
		RealtimeManager.forOrders.events.collect { action ->
			when (action) {
				is PostgresAction.Insert -> orders = orders.onChange(action)
                is PostgresAction.Update -> orders = orders.onChange(action)

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
            items(orders.size) {
                orders[it].Card(
                    modifier = Modifier.fillMaxWidth(),
                    onLocalUpdate = { updatedOrder ->
                        orders = orders.map {
                            if (it.orderId == updatedOrder.orderId) updatedOrder else it
                        }
                    }
                )
            }
		}
	}
}

@Composable
private fun Order.Card(
    modifier: Modifier = Modifier,
    onLocalUpdate: (Order) -> Unit
) {
    val order = this
    val scope = rememberCoroutineScope()
    var showCancelDialog by remember { mutableStateOf(false) }
    OutlinedCard(modifier) {
        Column(
            Modifier.padding(defaultPadding),
            Arrangement.spacedBy(defaultPadding),
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Order #$orderNumber",
                        style = MaterialTheme.typography.headlineMedium
                    )

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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, "Payment")
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Payment: ${if (paymentStatus) "Paid" else "Unpaid"}")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, "Amount")
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Total: $finalAmount")
                    }
                }
            }

            HorizontalDivider()

            OrderActionButtons(
                status = status,
                paymentStatus = paymentStatus,
                onCancel = {
                    showCancelDialog = true
                },
                onPayment = {
                    scope.launch {
                        confirmPayment(order)
                        onLocalUpdate(order.copy(paymentStatus = true))
                    }
                }
            )
        }
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancel Order") },
                text = { Text("Are you sure you want to cancel this order?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                            scope.launch {
                                cancelOrder(order)
                                onLocalUpdate(order.copy(status = Order.Status.CANCELED))
                            }
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showCancelDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
private fun OrderActionButtons(
    modifier: Modifier = Modifier,
    status: Order.Status?,
    paymentStatus: Boolean,
    onCancel: () -> Unit = {},
    onPayment: () -> Unit = {},
) = Row(
	modifier.fillMaxWidth(),
	Arrangement.spacedBy(defaultPadding, Alignment.End),
) {
    Button(
        onClick = onCancel,
        enabled = status != Order.Status.CANCELED
    ) {
        Text("Cancel")
    }

    Button(
        onClick = onPayment,
        enabled = !paymentStatus && status != Order.Status.CANCELED
    ) {
        Text("Confirm Payment")
    }
}

private fun List<Order>.onChange(action: PostgresAction.Insert): List<Order> {
	return this + action.decodeRecord<Order>()
}

private fun List<Order>.onChange(action: PostgresAction.Delete): List<Order> {
	return this.filterNot { it.orderId == action.decodeOldRecord<Order>().orderId }
}

private fun List<Order>.onChange(action: PostgresAction.Update): List<Order> {
    val updated = action.decodeRecord<Order>()
    return this.map {
        if (it.orderId == updated.orderId) updated else it
    }
}

private suspend fun cancelOrder(order: Order) {
    Order.markCancelled(order.orderId)
}

private suspend fun confirmPayment(order: Order) {
    Order.markPaid(order.orderId)
}

