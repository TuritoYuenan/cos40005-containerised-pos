package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.CheckoutTopBar
import containerised.pos.models.BranchItem
import containerised.pos.models.Currency
import containerised.pos.models.Order
import containerised.pos.routes.CustomerRoutes
import containerised.pos.services.CartService
import containerised.pos.services.CartService.getFinalAmount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCheckoutPage(navController: NavController?, args: CustomerRoutes.Checkout) {
	var isPlacingOrder by remember { mutableStateOf(false) }
	var checkoutItems by remember { mutableStateOf<List<CartService.Entry>>(emptyList()) }
	val scope = rememberCoroutineScope()

	// Hardcoded tax amount - assume it is gathered from settings stored in database
	val taxAmount = 0.2

	// Load cart items
	fun refreshCart() {
		checkoutItems = CartService.loadItems()
	}

	suspend fun placeOrder(isPayingAtCounter: Boolean) {
		if (checkoutItems.isEmpty()) {
			// TODO: Show a message if the cart is empty
			println("Cart is empty, cannot place order")
			return
		}

		isPlacingOrder = true
		val order = Order.Insertable(
			orderNumber = "001",
			orderType = resolveOrderType(args.tableID),
			tableNumber = args.tableID,
			status = Order.Status.PREPARING,
			branchId = args.branchID,
			taxAmount = taxAmount,
			finalAmount = checkoutItems.getFinalAmount(taxAmount)
		)

		val orderID = order.add()
		val orderItems = checkoutItems.map { entry -> entry.toOrderItem(orderID) }
		orderItems.forEach { it.add() }

//		After order is placed, clear the cart
		CartService.clear()
		refreshCart()
		isPlacingOrder = false

//		Navigate to payment page
		val route = CustomerRoutes.Payment(args.branchID, args.tableID, orderID, isPayingAtCounter)
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

	Scaffold(
		topBar = { CheckoutTopBar(navController) },
		contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp)
	) { paddingValues ->
		Column(
			Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
			Arrangement.spacedBy(16.dp)
		) {
			CartView(args, checkoutItems, isPlacingOrder) { refreshCart() }
			DiscountView()
			PaymentButtonsView(total, scope, isPlacingOrder, ::placeOrder)
		}
	}
}

/**
 * Resolves the order type based on the table ID.
 * The average table has the ID `TAB########`.
 */
private fun resolveOrderType(tableID: String): String = when (tableID) {
	"TAB00000000" -> "TAKEAWAY"
	"TAB11111111" -> "DELIVERY"
	else -> "DINE_IN"
}

@Composable
private fun CartView(
	args: CustomerRoutes.Checkout,
	checkoutItems: List<CartService.Entry>,
	isPlacingOrder: Boolean = false,
	onRefresh: () -> Unit
) {
	OutlinedCard(Modifier.fillMaxWidth()) {
		Column(
			Modifier.padding(16.dp),
			Arrangement.spacedBy(16.dp)
		) {
			Row {
				Icon(Icons.Filled.RoomService, "RoomService")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Order for Table ${args.tableID}", style = MaterialTheme.typography.titleMedium)
			}

			checkoutItems.forEach { CheckoutMenuItem(it.branchItem, it.count, onRefresh) }
		}

		if (isPlacingOrder) Box(
			Modifier
				.fillMaxWidth()
				.height(4.dp)
				.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
			Alignment.Center
		) {
			CircularProgressIndicator(Modifier.size(24.dp))
			Text(
				"Placing order...",
				color = MaterialTheme.colorScheme.surface,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = ButtonDefaults.IconSpacing)
			)
		}
	}
}

@Composable
private fun DiscountView() {
	OutlinedCard(Modifier.fillMaxWidth()) {
		Column(Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)) {
			Row {
				Icon(Icons.Filled.Discount, "Discount")
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text("Discounts and Promotions", style = MaterialTheme.typography.titleMedium)
			}

			Column(Modifier, Arrangement.spacedBy(8.dp)) {
				DiscountItemCard()
				DiscountItemCard()
			}
		}
	}
}

@Composable
private fun PaymentButtonsView(
	total: Double,
	scope: CoroutineScope,
	isPlacingOrder: Boolean = false,
	onPlaceOrder: suspend (isPayingAtCounter: Boolean) -> Unit = { _ -> }
) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		val currency = Currency.VND.code
		val formattedTotal = "$total $currency"

		// Cash Payment Button
		PaymentButton(
			label = "Pay at Counter",
			amount = formattedTotal,
			enabled = !isPlacingOrder,
			icon = Icons.Filled.Payment
		) {
			scope.launch { onPlaceOrder(true) }
		}

		// Bank Transfer Payment Button
		PaymentButton(
			label = "Self-checkout",
			amount = formattedTotal,
			enabled = !isPlacingOrder,
			icon = Icons.Filled.AccountBalance
		) {
			scope.launch { onPlaceOrder(false) }
		}
	}
}

@Composable
private fun CheckoutMenuItem(item: BranchItem, count: Int, onRefresh: () -> Unit) {
	val currency = Currency.VND.code
	val isSpecialNotesDialogOpen = remember { mutableStateOf(false) }

	Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp)) {
		// First Row: image and item details
		Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
			// Image placeholder
			Box(
				Modifier
					.size(76.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(MaterialTheme.colorScheme.primary),
				Alignment.Center
			) {}

			// Item details
			Column(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) {
				Text(
					item.itemName,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.titleMedium
				)

				Text(
					item.price.toString() + " " + currency,
					style = MaterialTheme.typography.bodyMedium
				)

				Text(
					"Total: " + (count * item.price) + " " + currency,
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}

		// Second Row: quantity counter and edit button (aligned to right)
		Row(
			Modifier.fillMaxWidth(),
			Arrangement.spacedBy(0.dp, Alignment.End),
			Alignment.CenterVertically
		) {
			// Edit button
			TextButton({ isSpecialNotesDialogOpen.value = true }) {
				Icon(Icons.Filled.Edit, "Edit")
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

				Text(count.toString())

				FilledTonalIconButton(
					onClick = {
						CartService.addOrIncreaseItem(item)
						onRefresh()
					},
				) { Icon(Icons.Filled.Add, "Increase") }
			}
		}
	}

	if (isSpecialNotesDialogOpen.value) {
		SpecialNotesDialog(item) { isSpecialNotesDialogOpen.value = false }
	}
}

@Composable
private fun DiscountItemCard() {
	val cardHeight = 64.dp

	Row(
		Modifier.fillMaxWidth(),
		Arrangement.spacedBy(16.dp),
	) {
		Box(
			Modifier
				.size(cardHeight)
				.clip(RoundedCornerShape(8.dp))
				.background(MaterialTheme.colorScheme.secondary),
			Alignment.Center
		) {}

		Column(
			Modifier.height(cardHeight),
			Arrangement.Center
		) {
			Text("Lorem Ipsum title", style = MaterialTheme.typography.titleSmall)
			Text("Lorem Ipsum condition", style = MaterialTheme.typography.bodySmall)
		}
	}
}

@Composable
private fun SpecialNotesDialog(item: BranchItem, onDismiss: () -> Unit) {
	val notes = remember {
		mutableStateOf(CartService.getItemNotes(item).orEmpty())
	}

	AlertDialog(
		onDismiss,
		confirmButton = {
			TextButton({
				CartService.updateItemNotes(item, notes.value)
				onDismiss()
			}) { Text("Save") }
		},
		dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
		icon = { Icon(Icons.Filled.Edit, "Edit") },
		title = { Text("Add Special Notes") },
		text = {
			OutlinedTextField(
				notes.value,
				{ notes.value = it },
				Modifier.fillMaxWidth(),
				placeholder = { Text("e.g. preferences, allergies") },
			)
		},
	)
}

@Composable
private fun PaymentButton(
	label: String,
	amount: String,
	icon: ImageVector,
	enabled: Boolean = true,
	onClick: () -> Unit
) {
	Button(
		onClick,
		Modifier.fillMaxWidth(),
		enabled,
		shape = RoundedCornerShape(12.dp),
	) {
		Row(
			Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(icon, label)
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Row(
				Modifier.fillMaxWidth(),
				Arrangement.SpaceBetween,
				Alignment.CenterVertically
			) {
				Text(label, style = MaterialTheme.typography.titleMedium)
				Text(amount, style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun PagePreview() = Column(
	Modifier.padding(16.dp),
	Arrangement.spacedBy(16.dp)
) {
	CartView(
		CustomerRoutes.Checkout("1", "5"),
		listOf(
			CartService.Entry(BranchItem.MOCK, 2),
			CartService.Entry(BranchItem.MOCK.copy(itemId = "2", itemName = "Bun Cha"), 1)
		)
	) {}

	DiscountView()

	PaymentButtonsView(150000.0, rememberCoroutineScope()) {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SpecialNotesDialogPreview() = Column(Modifier.fillMaxSize()) {
	SpecialNotesDialog(BranchItem.MOCK) { }
}
