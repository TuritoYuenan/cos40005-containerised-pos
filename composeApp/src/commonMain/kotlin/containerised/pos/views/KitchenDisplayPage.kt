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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import containerised.pos.NotificationService
import containerised.pos.database.OrderListener
import containerised.pos.models.Ingredient
import containerised.pos.models.Order
import containerised.pos.models.OrderItem
import containerised.pos.models.OrderStatus
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenDisplayPage(navController: NavController) {
	val scope = rememberCoroutineScope()
	var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
	var selectedOrder by remember { mutableStateOf<Order?>(null) }
	var selectedOrderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var error by remember { mutableStateOf<String?>(null) }

	val listener = remember {
		OrderListener(scope) { action ->
			when (action) {

				is PostgresAction.Insert -> {
					val newOrder = action.decodeRecord<Order>()
					println("Insert data: $newOrder")
					orders = orders + newOrder
					NotificationService.showNotification(
						title = "New Order",
						message = "Order #${newOrder.orderNumber} received"
					)
				}

				is PostgresAction.Update -> {
					val updated = action.decodeRecord<Order>()
					val old = action.decodeOldRecord<Order>()
					if (old.status == OrderStatus.PREPARING && updated.status == OrderStatus.FINISHED) {
						orders = orders.filterNot { it.orderId == old.orderId}
						println("deleted data: $old")
						NotificationService.showNotification(
							title = "Order Updated",
							message = "Order #${old.orderNumber} is done"
						)
					}
					else if (old.status == OrderStatus.PREPARING && updated.status == OrderStatus.CANCELED) {
						orders = orders.filterNot { it.orderId == old.orderId}
						println("deleted data: $old")
						NotificationService.showNotification(
							title = "Order Updated",
							message = "Order #${old.orderNumber} is canceled"
						)
					}
					else if ((updated.status == OrderStatus.FINISHED || updated.status == OrderStatus.CANCELED) && updated.status == OrderStatus.PREPARING){
						orders = orders + updated
						println("Insert data: $updated")
					}
					else{
						println("old data: $old")
						println("Updated data: $updated")
						orders = orders.map {
							if (it.orderId == updated.orderId) updated else it
						}
					}
				}

				is PostgresAction.Delete -> {
					val old = action.decodeOldRecord<Order>()
					println("Deleted → id=${old.orderId}")
					orders = orders.filterNot { it.orderId == old.orderId }
				}
				is PostgresAction.Select -> {

				}
			}
		}
	}

	val lifecycleOwner = LocalLifecycleOwner.current

	LaunchedEffect(Unit) {
		try {
			listener.initialize()
			orders = Order.fetchPreparing()
			println("Fetched ${orders.size} orders:")
		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}

	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			when (event) {
				Lifecycle.Event.ON_RESUME -> listener.subscribe()
				Lifecycle.Event.ON_PAUSE -> listener.unsubscribe()
				else -> {}
			}
		}

		lifecycleOwner.lifecycle.addObserver(observer)

		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
			listener.unsubscribe()
		}
	}

	LazyColumn {
		items(items = orders, key = { it.orderId }) { order ->
			KitchenDisplayOrderItem(
				order,
				onDone = { },
				onClickOrder = { selectedOrder = order; println(order) },
				onClickOrderItem = { selectedOrderItem -> selectedOrderItems = selectedOrderItem })
		}
	}
	selectedOrder?.let { order ->
		ExpandedOrderOverlay(
			order = order,
			onDone = { orders = orders.filterNot { it.orderId == order.orderId } },
			orderItems = selectedOrderItems,
			onDismiss = { selectedOrder = null }
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenDisplayOrderItem(
	order: Order,
	onDone: () -> Unit,
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
			orderItems.forEach { item ->
				println(
					"• ${item.itemId}: ${item.quantity} (${item.subtotal})"
				)
			}
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
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.primary)
			) {
				Box {}
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 6.dp, horizontal = 12.dp),
				) {
					Text(
						text = "Order #${order.orderNumber}",
						style = MaterialTheme.typography.titleMedium,
						color = Color.White,
					)
					Text(
						text = "Table No. ${order.tableNumber}",
						color = Color.White,
					)
				}
			}
			itemMap.forEach { (category, itemsOfCategory) ->
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 6.dp, horizontal = 12.dp),
				) {
					Text(
						text = category,
						style = MaterialTheme.typography.titleMedium,
					)
					itemsOfCategory.forEach { item ->
						Text(
							text = "${item?.quantity} x ${item?.branchItem?.itemName}",
							modifier = Modifier
								.clickable { expandedItemId = item?.itemId },
						)
						DropdownMenu(
							expanded = expandedItemId == item?.itemId,
							onDismissRequest = { expandedItemId = null }
						) {
							item?.branchItem?.itemIngredients?.forEach { itemIngredient ->
								DropdownMenuItem(
									text = { Text("${itemIngredient.quantity} (${itemIngredient.unit}) ${itemIngredient.ingredient.ingredientName}") },
									onClick = {
										expandedItemId = null
										println("${itemIngredient.quantity} (${itemIngredient.unit}) ${itemIngredient.ingredient.ingredientName}")
									}
								)
							}
						}
					}

				}
			}
			KitchenDisplayOrderButtons(scope, order, orderItems, onDone)
		}
	}
}

@Composable
fun KitchenDisplayOrderButtons(scope: CoroutineScope, order: Order, orderItems: List<OrderItem>, onDone: () -> Unit, onDismiss: (() -> Unit) = {}){
	Row(
		horizontalArrangement = Arrangement.spacedBy(
			10.dp,
			Alignment.End
		),
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp, horizontal = 12.dp),
	) {
		Button(
			onClick = {
				scope.launch {
					Order.markCancelled(order.orderId)
					onDone()
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
			Icon(
				Icons.Filled.Close,
				contentDescription = "Cancel"
			)
			Text("Cancel", color = Color.Black)
		}
		Button(
			onClick = {
				scope.launch {
					orderItems.forEach { orderItem ->
						orderItem.branchItem.itemIngredients.forEach { itemIngredient ->
							Ingredient.decreaseStock(itemIngredient.ingredientId, orderItem.quantity*(itemIngredient.quantity?: 0.0))
							println("decrease ${orderItem.quantity*(itemIngredient.quantity?: 0.0)} from ${itemIngredient.ingredientId}")
						}
					}

					Order.markFinished(order.orderId)
					onDone()
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
fun ExpandedOrderOverlay(
	order: Order,
	onDone: () -> Unit,
	orderItems: List<OrderItem>,
	onDismiss: () -> Unit,
) {
	val scope = rememberCoroutineScope()
	var itemMap by remember { mutableStateOf<Map<String, List<OrderItem?>>>(emptyMap()) }
	var expandedItemId by remember { mutableStateOf<String?>(null) }

	itemMap = orderItems.groupBy { it.branchItem.category.categoryName }
	Dialog(
		onDismissRequest = { onDismiss() },
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					indication = null,
					interactionSource = remember { MutableInteractionSource() }
				) {
					onDismiss()
				},
			contentAlignment = Alignment.Center
		) {

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight(0.8f)
					.clip(RoundedCornerShape(8.dp))
					.clickable( // consume click
						indication = null,
						interactionSource = remember { MutableInteractionSource() }
					) { },
				elevation = CardDefaults.cardElevation(12.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxHeight(),
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.background(MaterialTheme.colorScheme.primary)
					) {
						Box {}
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 6.dp, horizontal = 12.dp),
						) {
							Text(
								text = "Order #${order.orderNumber}",
								style = MaterialTheme.typography.titleMedium,
								color = Color.White,
							)
							Text(
								text = "Table No. ${order.tableNumber}",
								color = Color.White,
							)
						}
					}
					itemMap.forEach { (category, itemsOfCategory) ->
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 6.dp, horizontal = 12.dp),
						) {
							Text(
								text = category,
								style = MaterialTheme.typography.titleMedium,
							)
							itemsOfCategory.forEach { item ->
								Text(
									text = "${item?.quantity} x ${item?.branchItem?.itemName}",
									modifier = Modifier
										.clickable { expandedItemId = item?.itemId },
								)
								DropdownMenu(
									expanded = expandedItemId == item?.itemId,
									onDismissRequest = { expandedItemId = null }
								) {
									item?.branchItem?.itemIngredients?.forEach { itemIngredient ->
										DropdownMenuItem(
											text = { Text("${itemIngredient.quantity} (${itemIngredient.unit}) ${itemIngredient.ingredient.ingredientName}") },
											onClick = {
												expandedItemId = null
												println("${itemIngredient.quantity} (${itemIngredient.unit}) ${itemIngredient.ingredient.ingredientName}")
											}
										)
									}
								}
							}

						}
					}
					Spacer(modifier = Modifier.weight(1f))
					KitchenDisplayOrderButtons(scope, order, orderItems, onDone, onDismiss)
				}
			}
		}
	}
}
