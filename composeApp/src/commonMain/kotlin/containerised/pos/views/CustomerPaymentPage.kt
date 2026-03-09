package containerised.pos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import containerised.pos.components.BackButton
import containerised.pos.components.LoadingView
import containerised.pos.models.Bank
import containerised.pos.models.Currency
import containerised.pos.models.Order
import containerised.pos.models.PaymentCodeBuilder
import containerised.pos.routes.CustomerRoutes
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPaymentPage(navController: NavController, args: CustomerRoutes.Payment) {
	var order by remember { mutableStateOf<Order?>(null) }
	val scope = rememberCoroutineScope()

	LaunchedEffect(Unit) {
		scope.launch {
			order = Order.fetchByID(args.orderID) ?: return@launch
		}
	}

	Scaffold(
		topBar = { CustomerPaymentTopBar(navController) },
		contentWindowInsets = WindowInsets(16.dp)
	) { paddingValues ->
		Column(
			Modifier.fillMaxSize().padding(paddingValues),
			Arrangement.spacedBy(24.dp),
			Alignment.CenterHorizontally,
		) {
			when {
				// Show loading view while order is being fetched (i.e. empty)
				order == null -> LoadingView(Modifier.fillMaxWidth())

				// Show instructions for paying at counter
				args.isPayingAtCounter -> PayAtCounter(order)

				// Show QR code for self-checkout
				else -> SelfCheckout(order)
			}
		}

		val route = CustomerRoutes.Order(args.branchID, args.tableNumber)
		Button({ navController.navigate(route) }, Modifier.fillMaxWidth()) {
			Text("Back to Order Page")
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerPaymentTopBar(navController: NavController) = CenterAlignedTopAppBar(
	title = { Text("Payment") },
	navigationIcon = { BackButton(Modifier) { navController.popBackStack() } }
)

@Composable
private fun PayAtCounter(order: Order?) {
	Card(
		Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Column(
			Modifier.padding(24.dp),
			Arrangement.spacedBy(16.dp),
			Alignment.CenterHorizontally,
		) {
			Text(
				"Order Received",
				color = MaterialTheme.colorScheme.primary,
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
			)
			Text(
				"Your order ${order?.orderId ?: "..."} has been received. Please proceed to the counter to complete your payment.",
				Modifier.padding(horizontal = 16.dp),
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}

@Composable
private fun SelfCheckout(order: Order?) {
	val amount = order?.finalAmount ?: 0
	val currency = Currency.VND

	val paymentCode = if (order == null || amount <= 0) "" else PaymentCodeBuilder()
		.set(PaymentCodeBuilder.PIMethod.DYNAMIC)
		.set(PaymentCodeBuilder.ServiceCode.TRANSFER_TO_ACCOUNT)
		.setAccount(Bank.HDBank, "002704070021976")
		.setCountryCode()
		.setTransaction(amount, currency)
		.setPurpose("Payment for order ${order.orderId}")
		.build()

	val paymentQRCode = rememberQrCodePainter(paymentCode)

	Card(
		Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Column(
			Modifier.fillMaxWidth().padding(16.dp),
			Arrangement.spacedBy(8.dp),
			Alignment.CenterHorizontally,
		) {
			Text("Self-checkout", style = MaterialTheme.typography.titleMedium)
			Text(
				"$amount $currency",
				color = MaterialTheme.colorScheme.primary,
				style = MaterialTheme.typography.displaySmall,
				fontWeight = FontWeight.Bold,
			)
			Text("We accept VietQR bank transfer", style = MaterialTheme.typography.titleMedium)
			Image(paymentQRCode, "Payment QR Code")
		}
	}
}

@Preview
@Composable
private fun TopBarPreview() = CustomerPaymentTopBar(rememberNavController())

@Preview(showBackground = true)
@Composable
private fun PayAtCounterPreview() = Column(
	Modifier.padding(16.dp).fillMaxWidth(),
	Arrangement.spacedBy(24.dp),
	Alignment.CenterHorizontally,
) {
	PayAtCounter(Order.MOCK)
}

@Preview(showBackground = true)
@Composable
private fun SelfCheckoutPreview() = Column(
	Modifier.padding(16.dp).fillMaxWidth(),
	Arrangement.spacedBy(24.dp),
	Alignment.CenterHorizontally,
) {
	SelfCheckout(Order.MOCK)
}
