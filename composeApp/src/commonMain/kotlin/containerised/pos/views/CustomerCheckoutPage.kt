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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.CartEntry
import containerised.pos.CartService
import containerised.pos.models.BranchItem
import containerised.pos.models.Currency
import containerised.pos.models.OrderInsert
import containerised.pos.models.OrderStatus
import containerised.pos.routes.CustomerRoutes
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCheckoutPage(navController: NavController?, args: CustomerRoutes.Checkout) {
	var checkoutItems by remember { mutableStateOf<List<CartEntry>>(emptyList()) }
	val scope = rememberCoroutineScope()

	// Hardcoded tax amount - assume it is gathered from settings stored in database
	val taxAmount = 0.2

	// Load cart items
	fun refreshCart() {
		checkoutItems = CartService.loadItems()
	}

	suspend fun placeOrder(isPayingAtCounter: Boolean) {
		// Generate a unique order ID client-side to avoid database function permission issues
		val generatedOrderId = "ORD${Clock.System.now().toEpochMilliseconds()}"

		val order = OrderInsert(
			orderId = generatedOrderId,
			orderNumber = "001",
			orderType = "DINE_IN",
			tableNumber = args.tableNumber,
			status = OrderStatus.PREPARING,
			branchId = args.branchID,
			taxAmount = taxAmount,
			finalAmount = calculateFinalAmount(checkoutItems, taxAmount)
		)

		val orderItems = checkoutItems.map { entry -> entry.toOrderItem(generatedOrderId) }
		val orderID = order.addWithItems(orderItems)

//		After order creation, clear the cart
		CartService.clear()
		refreshCart()

//		Navigate to payment page
		val route = CustomerRoutes.Payment(args.branchID, args.tableNumber, orderID, isPayingAtCounter)
		navController?.navigate(route)
	}

	// Initial load
	LaunchedEffect(Unit) {
		refreshCart()
		println("Loaded cart items: $checkoutItems")
	}

	// Calculate total based on cart items
	val total = remember(checkoutItems) {
		checkoutItems.sumOf { entry -> entry.count * entry.branchItem.price.toDouble() }
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
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				shape = RoundedCornerShape(16.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(
					modifier = Modifier.background(Color.White).padding(12.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Row {
						Icon(Icons.Filled.RoomService, contentDescription = "RoomService")
						Text(text = "Table ${args.tableNumber}'s order", style = MaterialTheme.typography.titleMedium)
					}
					Column(Modifier.fillMaxWidth()) {
						checkoutItems.forEach { entry ->
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
				modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
				shape = RoundedCornerShape(16.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(modifier = Modifier.background(Color.White).padding(12.dp)) {
					Row(modifier = Modifier.padding(vertical = 8.dp)) {
						Icon(
							Icons.Filled.Discount,
							contentDescription = "Discount"
						)
						Text(
							text = "Discounts and Promotions",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(Modifier.fillMaxWidth()) {
						CheckoutDiscountItem()
						CheckoutDiscountItem()
					}
				}
			}

			Column(modifier = Modifier.background(Color.White).padding(12.dp)) {
				val currency = Currency.VND.code
				val formattedTotal = "$total $currency"

				// Cash Payment Button
				PaymentButton(
					label = "Place Order (Pay at Counter)",
					amount = formattedTotal,
					icon = Icons.Filled.Payment,
					iconCaption = "Cash"
				) {
					if (checkoutItems.isEmpty()) {
						// Show a message or disable the button if the cart is empty
						return@PaymentButton
					}
					scope.launch { placeOrder(isPayingAtCounter = true) }
				}

				// Bank Transfer Payment Button
				PaymentButton(
					label = "Place Order (VietQR Payment)",
					amount = formattedTotal,
					icon = Icons.Filled.AccountBalance,
					iconCaption = "Bank Transfer"
				) {
					if (checkoutItems.isEmpty()) {
						// Show a message or disable the button if the cart is empty
						return@PaymentButton
					}
					scope.launch { placeOrder(isPayingAtCounter = false) }
				}
			}
		}
	}
}

@Preview
@Composable
fun CheckoutMenuItem(item: BranchItem, count: Int, onRefresh: () -> Unit) {
	val currency = Currency.VND.code
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		// First Row: image and item details
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			// Image placeholder
			Box(
				modifier = Modifier
					.size(76.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0xFFACACAC)),
				contentAlignment = Alignment.Center
			) {}

			// Item details
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(
					text = item.itemName,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.titleMedium
				)

				Text(
					text = item.price.toString() + " " + currency,
					style = MaterialTheme.typography.bodyMedium
				)

				Text(
					text = "Total: " + (count * item.price) + " " + currency,
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}

		// Second Row: quantity counter and edit button (aligned to right)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.End),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Edit button
			TextButton(
				onClick = { /*TODO: Open dialogue to edit special notes for the order item*/ },
			) {
				Icon(Icons.Filled.Edit, contentDescription = "Edit")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Add special notes")
			}

			// Quantity counter
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				FilledTonalIconButton(
					onClick = {
						CartService.removeOrDecreaseItem(item)
						onRefresh()
					},
				) { Icon(Icons.Filled.Remove, "Decrease") }

				Text(text = count.toString())

				FilledTonalIconButton(
					onClick = {
						CartService.addOrIncreaseItem(item)
						onRefresh()
					},
				) { Icon(Icons.Filled.Add, "Increase") }
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
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.Top
		) {
			Icon(icon, iconCaption)
			Column(modifier = Modifier.fillMaxWidth()) {
				Text(label, style = MaterialTheme.typography.titleMedium)
				Text(amount, style = MaterialTheme.typography.titleMedium)
			}
		}
	}
}

fun calculateFinalAmount(cartItems: List<CartEntry>, taxAmount: Double): Int {
	val subtotal = cartItems.sumOf { entry -> entry.count * entry.branchItem.price }
	return (subtotal * (1 + taxAmount)).toInt()
}
