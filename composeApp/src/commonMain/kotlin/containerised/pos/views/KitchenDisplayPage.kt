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
		items(items = orders, key = { it.orderId }) { order ->
			KitchenDisplayOrderItem(
				order,
				onClickOrder = { selectedOrder = order; },
				onClickOrderItem = { selectedOrderItem -> selectedOrderItems = selectedOrderItem })
		}
	}
	selectedOrder?.let { order ->
		ExpandedOrderOverlay(
			order = order,
			orderItems = selectedOrderItems,
			onDismiss = { selectedOrder = null }
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KitchenDisplayOrderItem(
	order: Order,
	onClickOrder: () -> Unit,
	onClickOrderItem: (List<OrderItem>) -> Unit
) {
	val scope = rememberCoroutineScope()
	var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var itemMap by remember { mutableStateOf<Map<String, List<OrderItem?>>>(emptyMap()) }
	var expandedItemId by remember { mutableStateOf<String?>(null) }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			orderItems = OrderItem.fetchAndJoinOrderItemByOrder(order.orderId)
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
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.padding(vertical = 6.dp, horizontal = 12.dp)
			.clickable { onClickOrder(); onClickOrderItem(orderItems) },
	) {
		Column {
			Extracted(order, itemMap, expandedItemId)
			KitchenDisplayOrderButtons(scope, order, orderItems)
		}
	}
}

@Composable
private fun KitchenDisplayOrderButtons(
	scope: CoroutineScope,
	order: Order,
	orderItems: List<OrderItem>,
	onDismiss: (() -> Unit) = {}
) {
	Row(
		Modifier.fillMaxWidth().padding(12.dp, 6.dp),
		Arrangement.spacedBy(10.dp, Alignment.End),
		Alignment.CenterVertically,
	) {
		Button(
			onClick = {
				scope.launch {
					Order.markCancelled(order.orderId)
					onDismiss()
				}
			},
			shape = RoundedCornerShape(16.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.outlineVariant,
				contentColor = Color.Black
			),
			modifier = Modifier.height(40.dp)
		) {
			Icon(Icons.Filled.Close, "Cancel")
			Text("Cancel", color = Color.Black)
		}
		Button(
			onClick = {
				scope.launch {
					orderItems.forEach { orderItem ->
						orderItem.branchItem.itemIngredients.forEach { itemIngredient ->
							Ingredient.decreaseStock(
								itemIngredient.ingredientId,
								orderItem.quantity * (itemIngredient.quantity ?: 0.0)
							)
							println("decrease ${orderItem.quantity * (itemIngredient.quantity ?: 0.0)} from ${itemIngredient.ingredientId}")
						}
					}

					Order.markFinished(order.orderId)
					onDismiss()

				}
			},
			shape = RoundedCornerShape(16.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = Color.White
			),
			modifier = Modifier.height(40.dp)
		) {
			Icon(
				Icons.Filled.Check,
				contentDescription = "Done"
			)
			Text("Done", color = Color.White)
		}
	}
}

@Composable
private fun ExpandedOrderOverlay(
	order: Order,
	orderItems: List<OrderItem>,
	onDismiss: () -> Unit,
) {
	val scope = rememberCoroutineScope()
	val itemMap = orderItems.groupBy { it.branchItem.category.categoryName }
	var expandedItemId by remember { mutableStateOf<String?>(null) }

	Dialog(onDismissRequest = onDismiss) {
		Box(
			Modifier
				.fillMaxSize()
				.clickable(
					remember { MutableInteractionSource() },
					null,
					onClick = onDismiss
				),
			Alignment.Center
		) {
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight(0.8f)
					.clip(RoundedCornerShape(8.dp))
					.clickable(
						// consume click
						remember { MutableInteractionSource() },
						null,
					) { },
				elevation = CardDefaults.cardElevation(12.dp)
			) {
				Column(Modifier.fillMaxHeight()) {
					Extracted(order, itemMap, expandedItemId)
					Spacer(Modifier.size(ButtonDefaults.IconSpacing))
					KitchenDisplayOrderButtons(scope, order, orderItems, onDismiss)
				}
			}
		}
	}
}

@Composable
private fun Extracted(
	order: Order,
	itemMap: Map<String, List<OrderItem?>>,
	expandedItemId: String?
) {
	var mutableExpandedItemID = expandedItemId

	Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
		Box {}
		Column(Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 12.dp)) {
			Text(
				"Order #${order.orderNumber}",
				style = MaterialTheme.typography.titleMedium,
				color = Color.White,
			)
			Text("Table No. ${order.tableNumber}", color = Color.White)
		}
	}

	itemMap.forEach { (category, itemsOfCategory) ->
		Column(Modifier.fillMaxWidth().padding(12.dp, 6.dp)) {
			Text(category, style = MaterialTheme.typography.titleMedium)
			itemsOfCategory.forEach { item ->
				Text(
					"${item?.quantity} x ${item?.branchItem?.itemName}",
					Modifier.clickable { mutableExpandedItemID = item?.itemId },
				)
				DropdownMenu(
					expanded = mutableExpandedItemID == item?.itemId,
					onDismissRequest = { mutableExpandedItemID = null }
				) {
					item?.branchItem?.itemIngredients?.forEach {
						DropdownMenuItem(
							text = { Text("${it.quantity} (${it.unit}) ${it.ingredient.ingredientName}") },
							onClick = {
								mutableExpandedItemID = null
								println("${it.quantity} (${it.unit}) ${it.ingredient.ingredientName}")
							}
						)
					}
				}
			}
		}
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
private fun OrderItemPreview() = KitchenDisplayOrderItem(
	Order.MOCK, {}, {}
)

@Preview(apiLevel = 35)
@Composable
private fun ExpandedOrderOverlayPreview() = ExpandedOrderOverlay(
	Order.MOCK, OrderItem.MOCKS
) {}
