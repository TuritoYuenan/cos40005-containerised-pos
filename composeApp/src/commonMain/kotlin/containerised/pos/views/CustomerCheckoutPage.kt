package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.CartEntry
import containerised.pos.CheckoutItemStorage
import containerised.pos.CheckoutItemStorage.removeOrDecreaseItem
import containerised.pos.CheckoutItemStorage.addOrIncreaseItem
import containerised.pos.models.BranchItem
import containerised.pos.models.Currency
import containerised.pos.models.Order
import containerised.pos.models.OrderInsert
import containerised.pos.models.OrderStatus
import containerised.pos.routes.CustomerRoutes
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCheckoutPage(navController: NavController?) {
	var checkoutItems by remember { mutableStateOf<List<CartEntry>?>(null) }
	val tableNumber = 1
	val scope = rememberCoroutineScope()

	// Load cart items
	fun refreshCart() {
		checkoutItems = CheckoutItemStorage.loadItems()
	}

	suspend fun placeOrder(isPayingAtCounter: Boolean) {
		val order = OrderInsert(
			orderNumber = "001",
			orderType = "DINE_IN",
			tableNumber = tableNumber.toString(),
			status = OrderStatus.PREPARING,
			branchId = "BRA26011700",
			taxAmount = 0,
			finalAmount = checkoutItems?.sumOf { entry -> entry.count * entry.branchItem.price },
		)

		val orderID = Order.addWithItems(order, emptyList())

//		After order is created, clear the cart and navigate to payment if needed
		CheckoutItemStorage.clear()
		refreshCart()

		navController?.navigate(CustomerRoutes.Payment(orderID, isPayingAtCounter))
	}

	// Initial load
	LaunchedEffect(Unit) {
		refreshCart()
		println("Loaded cart items: $checkoutItems")
	}

	// Calculate total based on cart items
	val total = remember(checkoutItems) {
		checkoutItems?.sumOf { entry -> entry.count * entry.branchItem.price.toDouble() } ?: 0.0
	}

	LazyColumn {
		item {
			CenterAlignedTopAppBar(
				navigationIcon = {
					IconButton(onClick = { navController?.popBackStack() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				title = { Text("My Cart") }
			)
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				shape = RoundedCornerShape(12.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(modifier = Modifier.background(Color.White).padding(12.dp)) {
					Row(modifier = Modifier.padding(vertical = 6.dp)) {
						Icon(Icons.Filled.RoomService, contentDescription = "RoomService")
						Text(text = "Table $tableNumber's order", style = MaterialTheme.typography.titleMedium)
					}
					Column(
						modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						checkoutItems?.forEach { entry ->
							CheckoutMenuItem(
								item = entry.branchItem,
								count = entry.count,
								onRefresh = { refreshCart() }
							)
						}
					}
				}
			}

			Card(
				modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 12.dp),
				shape = RoundedCornerShape(12.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(modifier = Modifier.background(Color.White).padding(12.dp)) {
					Row(modifier = Modifier.padding(vertical = 6.dp)) {
						Icon(
							Icons.Filled.Discount,
							contentDescription = "Discount"
						)
						Text(
							text = "Discounts and Promotions",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						CheckoutDiscountItem()
						CheckoutDiscountItem()
					}
				}
			}

			Column(modifier = Modifier.background(Color.White).padding(12.dp)) {
				val currency = Currency.VND.code
				val formattedTotal = round(total * 100).div(100).toString() + currency

				// Bank Transfer Payment Button
				PaymentButton(
					label = "Place Order",
					amount = formattedTotal,
					icon = Icons.Filled.AccountBalance,
					iconCaption = "Bank Transfer"
				) {
					scope.launch {
						placeOrder(isPayingAtCounter = false)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun CheckoutMenuItem(item: BranchItem, count: Int, onRefresh: () -> Unit) {
	val currency = Currency.VND.code
	Box {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterHorizontally),
		) {
			Box(
				modifier = Modifier
					.size(76.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0xFFACACAC)),
				contentAlignment = Alignment.Center
			) {}
			Column(
				modifier = Modifier.height(76.dp).width(152.dp),
				verticalArrangement = Arrangement.SpaceEvenly
			) {
				Text(
					modifier = Modifier.width(82.dp),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					text = item.itemName,
					style = MaterialTheme.typography.titleSmall
				)

				Text(text = item.price.toString() + currency, style = MaterialTheme.typography.bodySmall)

				Row {
					Text(text = "Total: " + (count * item.price))
				}
			}

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.height(76.dp).width(114.dp),
			) {
				Button(
					modifier = Modifier.size(32.dp),
					onClick = {
						removeOrDecreaseItem(item)
						onRefresh()
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				) {
					Icon(Icons.Filled.Remove, contentDescription = "Decrease")
				}

				Text(text = count.toString())

				Button(
					modifier = Modifier.size(32.dp),
					onClick = {
						addOrIncreaseItem(item)
						onRefresh()
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				) {
					Icon(Icons.Filled.Add, contentDescription = "Increase")
				}
			}

			Button(
				modifier = Modifier.size(24.dp),
				onClick = {},
				colors = ButtonDefaults.buttonColors(containerColor = Color(0x00FFFFFF), contentColor = Color.Black),
				contentPadding = PaddingValues(0.dp)
			) {
				Icon(Icons.Filled.Edit, contentDescription = "Edit")
			}
		}
	}
}

@Preview
@Composable
fun CheckoutDiscountItem() {
	Row(
		modifier = Modifier
			.clip(RoundedCornerShape(8.dp))
			.background(MaterialTheme.colorScheme.outlineVariant)
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Box(
			modifier = Modifier
				.size(52.dp)
				.clip(RoundedCornerShape(8.dp))
				.background(Color(0xFFACACAC)),
			contentAlignment = Alignment.Center
		) {}

		Column(
			modifier = Modifier.height(52.dp),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(text = "Lorem Ipsum title", style = MaterialTheme.typography.titleSmall)
			Text(text = "Lorem Ipsum condition", style = MaterialTheme.typography.bodySmall)
		}
	}
}

@Composable
fun PaymentButton(label: String, amount: String, icon: ImageVector, iconCaption: String?, onClick: () -> Unit) {
	Button(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
		shape = RoundedCornerShape(8.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer,
			contentColor = MaterialTheme.colorScheme.onPrimaryContainer
		)
	) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(icon, contentDescription = iconCaption)
				Spacer(modifier = Modifier.width(8.dp))
				Text(text = label, style = MaterialTheme.typography.titleMedium)
			}
			Text(text = amount, style = MaterialTheme.typography.titleMedium)
		}
	}
}
