package containerised.pos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.models.Bank
import containerised.pos.models.Currency
import containerised.pos.models.CustomerPayment
import containerised.pos.models.PaymentCodeBuilder
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPaymentPage(navController: NavController, args: CustomerPayment) {
	val currencyNumericCode = Currency.fromCode(args.currency) ?: Currency.VND

	val paymentCode = PaymentCodeBuilder()
		.set(PaymentCodeBuilder.PIMethod.DYNAMIC)
		.set(PaymentCodeBuilder.ServiceCode.TRANSFER_TO_ACCOUNT)
		.setAccount(Bank.HDBank, "002704070021976")
		.setCountryCode()
		.setTransaction(args.amount, currencyNumericCode)
		.build()

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
						text = "${args.amount} ${args.currency}",
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

			Button(
				onClick = { navController.navigate("order") },
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Back to Order Page")
			}
		}
	}
}
