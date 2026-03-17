package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import containerised.pos.RealtimeManager
import containerised.pos.RealtimeServiceController
import containerised.pos.models.Ingredient
import containerised.pos.models.Order
import containerised.pos.models.OrderItem
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenDisplayPage() {
	var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
	var selectedOrder by remember { mutableStateOf<Order?>(null) }
	var selectedOrderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			orders = Order.fetchPreparing()
			println("Fetched ${orders.size} orders:")
		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}

	LaunchedEffect(Unit) {
		println("Creating channel")
		RealtimeServiceController.start()
		RealtimeManager.forOrders.start()
		RealtimeManager.forOrders.events.collect { action ->
			when (action) {
				is PostgresAction.Insert -> orders = orders.onChange(action)
				is PostgresAction.Update -> orders = orders.onChange(action)
				is PostgresAction.Delete -> orders = orders.onChange(action)
				is PostgresAction.Select -> {}
			}
		}
	}

	LazyColumn {
		items(orders, { it.orderId }) { order ->
			order.ItemCard(
				{ selectedOrder = order; },
				{ selectedOrderItems = it }
			)
		}
	}

	selectedOrder?.ExpandedOverlay(selectedOrderItems) { selectedOrder = null }
}

@Composable
private fun Order.ItemCard(
	onClickOrder: () -> Unit,
	onClickOrderItem: (List<OrderItem>) -> Unit
) {
	val scope = rememberCoroutineScope()
	var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var itemMap by remember { mutableStateOf<Map<String, List<OrderItem?>>>(emptyMap()) }
	val expandedItemId = remember { mutableStateOf<String?>(null) }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			orderItems = OrderItem.fetchByOrderWithJoins(orderId)
			println("Fetched ${orderItems.size} order items:")
			orderItems.forEach { item -> println("• ${item.itemId}: ${item.quantity} (${item.subtotal})") }

			itemMap = orderItems.groupBy { it.branchItem.category.categoryName }
			println(itemMap)
		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}

	Card(
		Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.padding(12.dp, 6.dp)
			.clickable { onClickOrder(); onClickOrderItem(orderItems) },
	) {
		Contents(itemMap, expandedItemId, scope, orderItems)
	}
}

/**
 * Action buttons for an order, allowing the user to mark the order as finished or cancelled.
 * @param scope Coroutine scope to launch the actions.
 * @param orderItems List of items associated with the order
 * @param onDismiss A callback to invoke after the action is completed, typically used to close the order details view.
 */
@Composable
private fun Order.ActionButtons(
	scope: CoroutineScope,
	orderItems: List<OrderItem>,
	onDismiss: () -> Unit = {}
) {
	Row(
		Modifier.fillMaxWidth().padding(12.dp, 6.dp),
		Arrangement.spacedBy(10.dp, Alignment.End),
		Alignment.CenterVertically,
	) {
		FilledTonalButton(
			onClick = {
				scope.launch {
					Order.markCancelled(orderId)
					onDismiss()
				}
			},
			shape = RoundedCornerShape(16.dp)
		) {
			Icon(Icons.Filled.Close, "Cancel")
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Text("Cancel")
		}

		Button(
			onClick = {
				scope.launch {
					orderItems.onComplete()
					Order.markFinished(orderId)
					onDismiss()
				}
			},
			shape = RoundedCornerShape(16.dp),
		) {
			Icon(Icons.Filled.Check, "Done")
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Text("Done")
		}
	}
}

@Composable
private fun Order.ExpandedOverlay(
	orderItems: List<OrderItem>,
	onDismiss: () -> Unit,
) = Dialog(onDismissRequest = onDismiss) {
	val scope = rememberCoroutineScope()
	val itemMap = orderItems.groupBy { it.branchItem.category.categoryName }
	val expandedItemId = remember { mutableStateOf<String?>(null) }

	Box(
		Modifier.fillMaxSize().clickable(
			remember { MutableInteractionSource() },
			null,
			onClick = onDismiss
		),
		Alignment.Center
	) {
		Card(
			modifier = Modifier
				.fillMaxWidth().fillMaxHeight(0.8f)
				.clip(RoundedCornerShape(8.dp))
				.clickable(
					// consume click
					remember { MutableInteractionSource() },
					null,
				) { },
			elevation = CardDefaults.cardElevation(12.dp)
		) {
			Contents(
				itemMap, expandedItemId, scope, orderItems,
				Modifier.fillMaxHeight(), onDismiss
			)
		}
	}
}

/**
 * Common layout to display order details, used in both the order card and the expanded overlay.
 */
@Composable
private fun Order.Contents(
	itemMap: Map<String, List<OrderItem?>>,
	expandedItemId: MutableState<String?>,
	scope: CoroutineScope,
	orderItems: List<OrderItem>,
	modifier: Modifier = Modifier,
	onDismiss: () -> Unit = { }
) = Column(modifier) {
	Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
		Box {}
		Column(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
			Text(
				"Order #${orderNumber}",
				style = MaterialTheme.typography.titleMedium,
				color = Color.White,
			)
			Text("Table No. ${table?.tableCode}", color = Color.White)
		}
	}

	itemMap.forEach { (category, itemsOfCategory) ->
		Column(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
			Text(category, style = MaterialTheme.typography.titleMedium)
			itemsOfCategory.forEach { item ->
				Text(
					"${item?.quantity} x ${item?.branchItem?.itemName}",
					Modifier.clickable { expandedItemId.value = item?.itemId },
				)
				DropdownMenu(
					expanded = expandedItemId.value == item?.itemId,
					onDismissRequest = { expandedItemId.value = null }
				) {
					item?.branchItem?.itemIngredients?.forEach {
						DropdownMenuItem(
							text = { Text("${it.quantity} (${it.unit}) ${it.ingredient.ingredientName}") },
							onClick = {
								expandedItemId.value = null
								println("${it.quantity} (${it.unit}) ${it.ingredient.ingredientName}")
							}
						)
					}
				}
			}
		}
	}

	Spacer(Modifier.size(ButtonDefaults.IconSpacing))
	this@Contents.ActionButtons(scope, orderItems, onDismiss)
}

private suspend fun List<OrderItem>.onComplete() = forEach { orderItem ->
	orderItem.branchItem.itemIngredients.forEach { itemIngredient ->
		val amount = orderItem.quantity * (itemIngredient.quantity ?: 0.0)
		Ingredient.decreaseStock(itemIngredient.ingredientId, amount)
		println("decrease $amount from ${itemIngredient.ingredientId}")
	}
}

private fun List<Order>.onChange(action: PostgresAction.Insert): List<Order> {
	return this + action.decodeRecord<Order>()
}

private fun List<Order>.onChange(action: PostgresAction.Delete): List<Order> {
	return this.filterNot { it.orderId == action.decodeOldRecord<Order>().orderId }
}

private fun List<Order>.onChange(action: PostgresAction.Update): List<Order> {
	val new = action.decodeRecord<Order>()
	val old = action.decodeOldRecord<Order>()
	val (isPtoF, isPtoC, isFCtoP) = new.inferStatusChange(old)

	return when {
		// If the update is about status, add/remove the order accordingly
		isFCtoP -> this + new
		isPtoF || isPtoC -> this.filterNot { it.orderId == old.orderId }

		// If the update is about something else, update the order in place
		else -> this.map { if (it.orderId == new.orderId) new else it }
	}
}

@Preview(apiLevel = 35)
@Composable
private fun OrderItemPreview() = Order.MOCK.ItemCard(
	{}, {}
)

@Preview(apiLevel = 35)
@Composable
private fun ExpandedOrderOverlayPreview() = Order.MOCK.ExpandedOverlay(
	OrderItem.MOCKS
) {}
