package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.LoadingView
import containerised.pos.components.PayAtCounterView
import containerised.pos.components.PaymentTopBar
import containerised.pos.components.SelfCheckoutView
import containerised.pos.models.Order
import containerised.pos.routes.CustomerRoutes
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

	val route = CustomerRoutes.Order(args.branchID, args.tableID)
	Scaffold(
		topBar = { PaymentTopBar { navController.navigate(route) } },
		contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
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
				args.isPayingAtCounter -> PayAtCounterView(order)

				// Show QR code for self-checkout
				else -> SelfCheckoutView(order)
			}
		}
	}
}
