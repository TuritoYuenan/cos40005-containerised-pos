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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.CheckoutItemStorage
import containerised.pos.models.BranchItem
import containerised.pos.models.Currency
import containerised.pos.models.CustomerPayment
import containerised.pos.models.fetchBranchItemByBranch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CustomerCheckoutPage(navController: NavController?) {
	var menuItems by remember { mutableStateOf<List<BranchItem>?>(null) }
	var error by remember { mutableStateOf<String?>(null) }
	var checkoutItemFromStorage by remember { mutableStateOf<List<BranchItem>?>(null) }


	val sampleBranchId = "BRA26011700"
	LaunchedEffect(Unit) {
		try {
			menuItems = fetchBranchItemByBranch(sampleBranchId)
			println("Fetched ${menuItems!!.size} menu items:")
			menuItems!!.forEach { item ->
				println(
					"• ${item.itemId}: ${item.itemName} (${item.price})"
				)
			}
			CheckoutItemStorage.clear()
			CheckoutItemStorage.saveItems(menuItems!!)

			checkoutItemFromStorage = CheckoutItemStorage.loadItems()
			println(checkoutItemFromStorage ?: "No stored items")
		} catch (e: Exception) {
			error = e.message
			println("Error: $error")
		}
	}
	val itemSum = remember(menuItems) {
		mutableStateMapOf<String, Double>().apply {
			menuItems?.forEach { item -> put(item.itemId, item.price.toDouble()) }
	} }
	val total = itemSum.values.sum()
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
				Column(
					modifier = Modifier
						.background(Color.White)
				) {
					Row(
						modifier = Modifier
							.padding(vertical = 6.dp),
					) {
						Icon(
							Icons.Filled.RoomService,
							contentDescription = "RoomService"
						)
						Text(
							text = "Table 1's order",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						checkoutItemFromStorage?.forEach { item -> CheckoutMenuItem(item, sum = itemSum[item.itemId] ?: 0.0, onValueChange = {newValue -> itemSum[item.itemId] = newValue; println("$newValue, $itemSum")}) }
					}
				}
			}
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 6.dp, horizontal = 12.dp),
				shape = RoundedCornerShape(12.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(
					modifier = Modifier
						.background(Color.White)
				) {
					Row(
						modifier = Modifier
							.padding(vertical = 6.dp),
					) {
						Icon(
							Icons.Filled.Discount,
							contentDescription = "Decrease"
						)
						Text(
							text = "Table 1's discount",
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 3.dp, horizontal = 12.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp)
					) {
						Column(
							modifier = Modifier
								.fillMaxWidth(),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							CheckoutDiscountItem()
							CheckoutDiscountItem()
						}
					}
				}
			}
			Box {
				val currency = Currency.VND.code

				Card(
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.surface)
						.padding(vertical = 6.dp, horizontal = 6.dp)
						.align(Alignment.BottomCenter),
					onClick = {
						navController?.navigate(CustomerPayment(amount = total, currency = currency))
					}
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.clip(RoundedCornerShape(2.dp))
							.background(MaterialTheme.colorScheme.primaryContainer)
							.padding(vertical = 6.dp, horizontal = 6.dp),

						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text("Order:")
						Text(round(total*100).div(100).toString() + currency)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun CheckoutMenuItem(item: BranchItem, sum: Double, onValueChange: (Double) -> Unit) {
	var value by remember { mutableStateOf<Int>(1) }
	val currency = Currency.VND.code
	Box {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
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
				modifier = Modifier
					.height(76.dp)
					.width(152.dp),

				verticalArrangement = Arrangement.SpaceEvenly
			) {
				Text(
					modifier = Modifier
						.width(82.dp),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					text = item.itemName,
					style = MaterialTheme.typography.titleSmall
				)
				Text(
					text = item.price.toString() + currency,
					style = MaterialTheme.typography.bodySmall
				)
				Row {
					Text(
						text = "Total: " + round(value*item.price*100)/100 + currency,
					)
				}
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier
					.height(76.dp)
					.width(114.dp),
			){
				Button(
					modifier = Modifier.size(32.dp),
					onClick = { value--; onValueChange((value*item.price).toDouble())},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				) {
					Icon(
						Icons.Filled.Remove,
						contentDescription = "Decrease"
					)
				}
				Text(
					text = value.toString(),
				)
				Button(
					modifier = Modifier
						.size(32.dp),
					onClick = {value++; onValueChange((value*item.price).toDouble()) },
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = Color.Black
					),
					shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
					contentPadding = PaddingValues(0.dp)
				){
					Icon(Icons.Filled.Add, contentDescription = "Decrease")
				}
			}
			Button(
				modifier = Modifier
					.size(24.dp),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0x00FFFFFF),
					contentColor = Color.Black
				),
				contentPadding = PaddingValues(0.dp)
			){
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
			modifier = Modifier
				.height(52.dp),

			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = "Lorem isum title",
				style = MaterialTheme.typography.titleSmall
			)
			Text(
				text = "Lorem isum condition",
				style = MaterialTheme.typography.bodySmall
			)
		}
	}
}
