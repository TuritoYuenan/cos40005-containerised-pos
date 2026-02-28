package containerised.pos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
	val currency = Currency.VND

	val scope = rememberCoroutineScope()

	LaunchedEffect(Unit) {
		scope.launch {
			order = Order.fetchByID(args.orderID) ?: return@launch
		}
	}

	val amount = order?.finalAmount ?: 0

	val paymentCode = if (order != null && amount > 0) {
		PaymentCodeBuilder()
			.set(PaymentCodeBuilder.PIMethod.DYNAMIC)
			.set(PaymentCodeBuilder.ServiceCode.TRANSFER_TO_ACCOUNT)
			.setAccount(Bank.HDBank, "002704070021976")
			.setCountryCode()
			.setTransaction(amount, currency)
			.build()
	} else {
		""
	}

	val paymentQRCode = rememberQrCodePainter(paymentCode)

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				title = { Text("Payment") }
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			if (args.isPayingAtCounter) {
				// Paying at counter - show confirmation message
				Card(
					modifier = Modifier.fillMaxWidth(),
					elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(24.dp),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(16.dp)
					) {
						Text(
							text = "Order Received",
							style = MaterialTheme.typography.headlineMedium,
							color = MaterialTheme.colorScheme.primary,
							fontWeight = FontWeight.Bold
						)
						Text(
							text = "Your order ${order?.orderId ?: "..."} has been received. Please proceed to the counter to complete your payment.",
							style = MaterialTheme.typography.bodyLarge,
							modifier = Modifier.padding(horizontal = 16.dp)
						)
					}
				}
			} else {
				// Online payment - show QR code
				if (order == null) {
					CircularProgressIndicator()
				} else {
					Card(
						modifier = Modifier.fillMaxWidth(),
						elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
					) {
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(24.dp),
							horizontalAlignment = Alignment.CenterHorizontally,
							verticalArrangement = Arrangement.spacedBy(8.dp)
						) {
							Text(
								text = "Total Amount",
								style = MaterialTheme.typography.titleMedium
							)
							Text(
								text = "$amount $currency",
								style = MaterialTheme.typography.displaySmall,
								color = MaterialTheme.colorScheme.primary,
								fontWeight = FontWeight.Bold,
							)
						}
					}

					Text(
						text = "Scan the QR code below to pay",
						style = MaterialTheme.typography.titleMedium
					)

					Image(
						painter = paymentQRCode,
						contentDescription = "Payment QR Code",
					)
				}
			}

			val route = CustomerRoutes.Order(branchID = args.branchID, tableNumber = args.tableNumber)
			Button(
				onClick = { navController.navigate(route) },
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Back to Order Page")
			}
		}
	}
}
