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
import androidx.navigation.NavController
import containerised.pos.models.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun KitchenDisplayPage(navController: NavController) {
	var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
	var selectedOrder by remember { mutableStateOf<Order?>(null) }
	var selectedOrderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var error by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(Unit) {
		try {
			orders = fetchPreparingOrders()
			println("Fetched ${orders.size} orders:")

		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}

	LazyColumn {
		items(items = orders, key = { it.orderId }) { order ->
			KitchenDisplayOrderItem(
				order,
				onDone = {orders = orders.filterNot { it.orderId == order.orderId }},
				onClickOrder = {selectedOrder = order; println(order)},
				onClickOrderItem = {selectedOrderItem -> selectedOrderItems = selectedOrderItem})
		}
	}
	selectedOrder?.let { order ->
		ExpandedOrderOverlay(
			order = order,
			onDone = {orders = orders.filterNot { it.orderId == order.orderId }},
			orderItems = selectedOrderItems,
			onDismiss = { selectedOrder = null }
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun KitchenDisplayOrderItem(
	order: Order,
	onDone: () -> Unit,
	onClickOrder: () -> Unit,
	onClickOrderItem: (List<OrderItem>) -> Unit
){
	val scope = rememberCoroutineScope()
	var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
	var itemMap by remember { mutableStateOf<Map<String, List<OrderItem?>>>(emptyMap()) }
	var expandedItemId by remember { mutableStateOf<String?>(null) }
	var error by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		try {
			orderItems = fetchAndJoinOrderItemByOrder(order.orderId)
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
			.clickable { onClickOrder(); onClickOrderItem(orderItems)},
	){
		Column {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.primary)
			) {
				Box{}
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
			itemMap.forEach { (category, itemsOfCategory)->
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 6.dp, horizontal = 12.dp),
				){
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
			Row(
				horizontalArrangement = Arrangement.spacedBy(
					10.dp,
					Alignment.End
				),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
			){
				Button(
					onClick = { },
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
							markOrderAsFinished(order.orderId)
							onDone()
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
						Box{}
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
					itemMap.forEach { (category, itemsOfCategory)->
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 6.dp, horizontal = 12.dp),
						){
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
					Row(
						horizontalArrangement = Arrangement.spacedBy(
							10.dp,
							Alignment.End
						),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 6.dp, horizontal = 12.dp),
					){
						Button(
							onClick = { },
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
									markOrderAsFinished(order.orderId)
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
			}
		}
	}
}
