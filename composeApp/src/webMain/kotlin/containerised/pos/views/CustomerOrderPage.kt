package containerised.pos.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.LoadingView
import containerised.pos.components.OrderSearchBar
import containerised.pos.components.CartFAB
import containerised.pos.components.ImageSlider
import containerised.pos.components.MenuTags
import containerised.pos.components.TallItemCard
import containerised.pos.components.WideItemCard
import containerised.pos.models.BranchItem
import containerised.pos.models.Tag
import containerised.pos.routes.CustomerRoutes
import containerised.pos.services.CartService
import kotlinx.coroutines.launch

private val defaultPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderPage(navController: NavController?, args: CustomerRoutes.Order) {
	//	Search state
	val textFieldState = remember { TextFieldState() }
	var searchResults by remember { mutableStateOf(listOf<String>()) }

	// State for items and tags
	var featuredItems by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
	var cartItems by remember { mutableStateOf<List<CartService.Entry>>(emptyList()) }
	var allItems by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
	var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }

	//	Data fetching state
	var isLoading by remember { mutableStateOf(true) }
	val scope = rememberCoroutineScope()

	fun refreshCart() {
		cartItems = CartService.loadItems()
	}

	fun BranchItem.addToCart() {
		println("Adding item to cart: $itemName")
		CartService.addOrIncreaseItem(this)
		refreshCart()
	}

	// Fetch data on startup
	LaunchedEffect(Unit) {
		scope.launch {
			try {
				tags = Tag.fetchAll()
				allItems = BranchItem.fetchAll()
				featuredItems = allItems.filter { it.isFeatured }
			} catch (e: Exception) {
				// Handle error - you might want to show an error message
				println("Error fetching data: ${e.message}")
			} finally {
				isLoading = false
			}
		}
	}

	Scaffold(
		topBar = {
			OrderSearchBar(textFieldState, Modifier, searchResults) {
				/*TODO: Implement search logic here*/
			}
		},
		bottomBar = {
			AnimatedVisibility(
				!cartItems.isEmpty(),
				enter = slideInVertically(initialOffsetY = { it }),
				exit = slideOutVertically(targetOffsetY = { it }),
				label = "Cart Bottom Bar"
			) {
				CartStatusBar(navController, args, cartItems)
			}
		}
	) { paddingValues ->
		if (isLoading) {
			LoadingView(Modifier.fillMaxSize().padding(paddingValues))
			return@Scaffold
		}

		LazyColumn(
			Modifier.padding(paddingValues),
			verticalArrangement = Arrangement.spacedBy(defaultPadding)
		) {
			// Table Info
			item {
				Text(
					"Ordering for Table ${args.tableNumber}",
					Modifier.padding(8.dp).fillMaxWidth(),
					style = MaterialTheme.typography.bodyLarge,
					textAlign = TextAlign.Center
				)

				// Image Slider
				ImageSlider(allItems, Modifier.padding(8.dp, 0.dp))

				// Featured Section
				Text("Featured", Modifier.padding(defaultPadding), style = MaterialTheme.typography.headlineMedium)

				LazyRow(
					Modifier.padding(8.dp, 0.dp),
					horizontalArrangement = Arrangement.spacedBy(defaultPadding),
				) {
					items(featuredItems.size) { index ->
						TallItemCard(featuredItems[index]) {
							featuredItems[index].addToCart()
						}
					}
				}

				// Browse Section
				Text(
					"Browse all menu items",
					Modifier.padding(defaultPadding),
					style = MaterialTheme.typography.headlineMedium
				)
				MenuTags(tags)
			}

			// Search results items
			items(allItems.size) { index ->
				WideItemCard(allItems[index], Modifier.padding(8.dp, 0.dp)) {
					allItems[index].addToCart()
				}
			}
		}
	}
}

@Composable
private fun CartStatusBar(
	navController: NavController?,
	args: CustomerRoutes.Order,
	cartItems: List<CartService.Entry>,
) {
	val total = cartItems.sumOf { entry -> entry.count * entry.branchItem.price.toDouble() }
	val status = "${cartItems.size} items\nTotal: $total VND"

	BottomAppBar(
		actions = {
			Text(
				status,
				Modifier.padding(8.dp, 0.dp),
				style = MaterialTheme.typography.titleMedium
			)
		},
		floatingActionButton = {
			CartFAB {
				navController?.navigate(CustomerRoutes.Checkout(args.branchID, args.tableNumber))
			}
		}
	)
}

@Preview(apiLevel = 35)
@Composable
private fun CartStatusBarPreview() = CartStatusBar(
	null, CustomerRoutes.Order("branchID", "tableNumber"), listOf(
		CartService.Entry(BranchItem.MOCK, 2),
		CartService.Entry(BranchItem.MOCK.copy(itemName = "Another Item"), 1)
	)
)
