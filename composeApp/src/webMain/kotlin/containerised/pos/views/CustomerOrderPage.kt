package containerised.pos.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.components.*
import containerised.pos.models.BranchItem
import containerised.pos.models.Tag
import containerised.pos.routes.CustomerRoutes
import containerised.pos.services.CartService
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

private val defaultPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderPage(navController: NavController?, args: CustomerRoutes.Order) {
	//	Search and filter state
	val textFieldState = remember { TextFieldState() }
	var searchResults by remember { mutableStateOf(listOf<String>()) }
	var selectedTags by remember { mutableStateOf(setOf<String>()) }

	// State for items and tags
	var cartItems by remember { mutableStateOf<List<CartService.Entry>>(emptyList()) }
	var allItems by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
	var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }

	//	Data fetching state
	var isLoading by remember { mutableStateOf(true) }
	var error by remember { mutableStateOf<String?>(null) }
	val scope = rememberCoroutineScope()

	fun BranchItem.addToCart() {
		println("Adding item to cart: $itemName")
		CartService.addOrIncreaseItem(this)
		cartItems = CartService.loadItems()
	}

	fun List<BranchItem>.filteredByTags(): List<BranchItem> {
		return if (selectedTags.isEmpty()) this else this.filter { item ->
			val itemTagNames = item.itemTags?.map { it.tag?.tagId } ?: emptyList()
			itemTagNames.any { it in selectedTags }
		}
	}

	fun List<CartService.Entry>.cartInfo(): Pair<Int, Int> = Pair(
		this.sumOf { it.count },
		this.sumOf { it.count * it.branchItem.price }
	)

	// Fetch data on startup
	LaunchedEffect(Unit) {
		scope.launch {
			try {
				tags = Tag.fetchAll()
				allItems = BranchItem.fetchByBranchWithDetails(args.branchID)
			} catch (e: Exception) {
				error = e.message.also { println(it) }
			} finally {
				isLoading = false
			}
		}
	}

	Scaffold(
		topBar = {
			OrderSearchBar(textFieldState, Modifier, searchResults) {
				searchResults = allItems.filter {
					it.itemName.contains(it.itemName, true)
				}.map { it.itemName }
			}
		},
		bottomBar = {
			AnimatedVisibility(
				!cartItems.isEmpty(),
				enter = slideInVertically(initialOffsetY = { it }),
				exit = slideOutVertically(targetOffsetY = { it }),
				label = "Cart Bottom Bar"
			) {
				val (itemCount, totalPrice) = cartItems.cartInfo()
				CartStatusBar(itemCount, totalPrice) {
					navController?.navigate(
						CustomerRoutes.Checkout(args.branchID, args.tableID)
					)
				}
			}
		}
	) { paddingValues ->
		if (isLoading) return@Scaffold LoadingView(
			Modifier.fillMaxSize().padding(paddingValues)
		)

		if (error != null) return@Scaffold ErrorView(
			error!!,
			Modifier.fillMaxSize().padding(paddingValues)
		)

		LazyColumn(
			Modifier.padding(paddingValues),
			verticalArrangement = Arrangement.spacedBy(defaultPadding)
		) {
			val featured = allItems.filter { it.isFeatured }
			val filtered = allItems.filteredByTags()

			// Table Info
			item {
				Text(
					"Ordering for Table ${args.tableID}",
					Modifier.padding(8.dp).fillMaxWidth(),
					style = MaterialTheme.typography.bodyLarge,
					textAlign = TextAlign.Center
				)

				// Image Slider
				featured.ImageSlider(Modifier.padding(8.dp, 0.dp))

				// Featured Section
				Text(
					"Featured",
					Modifier.padding(defaultPadding),
					style = MaterialTheme.typography.headlineMedium
				)

				LazyRow(
					Modifier.padding(8.dp, 0.dp),
					horizontalArrangement = Arrangement.spacedBy(defaultPadding),
				) {
					items(featured.size) { i ->
						featured[i].TallCard { featured[i].addToCart() }
					}
				}

				// Browse Section
				Text(
					"Browse all menu items",
					Modifier.padding(defaultPadding),
					style = MaterialTheme.typography.headlineMedium
				)

				tags.Row {
					selectedTags = if (selectedTags.contains(it)) {
						selectedTags - it
					} else {
						selectedTags + it
					}
				}
			}

			// Display all items for browsing, manipulated by tags
			items(filtered.size) { i ->
				filtered[i].WideCard(Modifier.padding(horizontal = 8.dp)) {
					filtered[i].addToCart()
				}
			}
		}
	}
}
