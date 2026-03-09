package containerised.pos.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.CartEntry
import containerised.pos.CartService
import containerised.pos.components.LoadingView
import containerised.pos.models.BranchItem
import containerised.pos.models.Tag
import containerised.pos.routes.CustomerRoutes
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
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
	var cartItems by remember { mutableStateOf<List<CartEntry>>(emptyList()) }
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
			TopSearchBar(textFieldState, Modifier, searchResults) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopSearchBar(
	textFieldState: TextFieldState,
	modifier: Modifier = Modifier,
	searchResults: List<String> = emptyList(),
	onSearch: (String) -> Unit,
) {
	// Controls expansion state of the search bar
	var expanded by rememberSaveable { mutableStateOf(false) }

	Box(modifier.semantics { isTraversalGroup = true }) {
		SearchBar(
			{
				SearchBarDefaults.InputField(
					query = textFieldState.text.toString(),
					onQueryChange = { textFieldState.edit { replace(0, length, it) } },
					onSearch = {
						onSearch(textFieldState.text.toString())
						expanded = false
					},
					expanded = expanded,
					onExpandedChange = { expanded = it },
					placeholder = { Text("Search menu items") },
					leadingIcon = {
						Icon(Icons.Filled.Search, "Search Icon")
					}
				)
			},
			expanded,
			{ expanded = it },
			Modifier
				.fillMaxWidth()
				.widthIn(32.dp, 512.dp)
				.padding(8.dp)
				.semantics { traversalIndex = 0f },
		) {
			if (searchResults.isEmpty()) {
				Text("No results found", Modifier.padding(16.dp))
				return@SearchBar
			}

			Column {
				searchResults.forEach { result ->
					Text(result, Modifier.padding(16.dp))
					HorizontalDivider()
				}
			}
		}
	}
}

@Composable
private fun ImageSlider(items: List<BranchItem>, modifier: Modifier) {
	val pagerState = rememberPagerState(pageCount = { items.size })

	Card(modifier.widthIn(0.dp, 512.dp).aspectRatio(2f)) {
		if (items.isNotEmpty()) {
			HorizontalPager(pagerState, Modifier.fillMaxSize()) { page ->
				KamelImage(
					{ asyncPainterResource("https://placehold.co/512x256") },
					items[page % items.size].itemName,
					Modifier.fillMaxSize(),
					contentScale = ContentScale.Crop,
					onFailure = {
						Box(
							Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
							Alignment.Center,
						) {}
					}
				)
			}
		} else {
			Box(
				Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
				Alignment.Center,
			) {
				Text("No items available", color = MaterialTheme.colorScheme.onPrimary)
			}
		}
	}
}

@Composable
private fun MenuTags(tags: List<Tag>) {
	LazyRow(
		Modifier.padding(8.dp, 0.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		items(tags.size) { index ->
			InputChip(
				false,
				{ /*TODO: Implement tag filtering*/ },
				{ Text(tags[index].tagName) },
			)
		}
	}
}

@Composable
private fun CartStatusBar(
	navController: NavController?,
	args: CustomerRoutes.Order,
	cartItems: List<CartEntry>,
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
			ExtendedFloatingActionButton(
				text = { Text("View Cart") },
				icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
				onClick = { navController?.navigate(CustomerRoutes.Checkout(args.branchID, args.tableNumber)) },
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
			)
		}
	)
}

@Composable
private fun TallItemCard(item: BranchItem, onAddToCart: () -> Unit = {}) {
	val cardWidth = 128.dp

	OutlinedCard(Modifier.size(cardWidth, 256.dp)) {
		Box {
			Column {
				KamelImage(
					resource = { asyncPainterResource("https://placehold.co/256x256") },
					contentDescription = item.itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(cardWidth)
						.aspectRatio(1f)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier
								.size(cardWidth)
								.clip(RoundedCornerShape(8.dp))
								.background(MaterialTheme.colorScheme.primary),
						) {}
					}
				)

				ItemMetadata(item)
			}

			// Add to cart button positioned at top-right
			AddToCartButton(onAddToCart, Modifier.align(Alignment.TopEnd))
		}
	}
}

@Composable
private fun WideItemCard(item: BranchItem, modifier: Modifier = Modifier, onAddToCart: () -> Unit = {}) {
	val cardHeight = 128.dp

	OutlinedCard(modifier.width(384.dp).height(cardHeight)) {
		Box {
			Row(Modifier.fillMaxWidth()) {
				KamelImage(
					resource = { asyncPainterResource("https://placehold.co/256x256") },
					contentDescription = item.itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(cardHeight)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier
								.size(cardHeight)
								.clip(RoundedCornerShape(8.dp))
								.background(MaterialTheme.colorScheme.primary),
						) {}
					}
				)
				ItemMetadata(item)
			}

			// Add to cart button positioned at top-right
			AddToCartButton(onAddToCart, Modifier.align(Alignment.TopEnd))
		}
	}
}

@Composable
private fun ItemMetadata(item: BranchItem) {
	Column(Modifier.padding(8.dp)) {
		Text(
			item.itemName,
			maxLines = 2,
			overflow = TextOverflow.Ellipsis,
			style = MaterialTheme.typography.titleMedium
		)
		item.itemDes?.let {
			Text(
				it,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodyMedium
			)
		}
		Text("${item.price} VND", style = MaterialTheme.typography.bodyLarge)
	}
}

@Composable
private fun AddToCartButton(onAddToCart: () -> Unit, modifier: Modifier = Modifier) {
	FilledTonalIconButton(onAddToCart, modifier.padding(8.dp).size(32.dp)) {
		Icon(Icons.Filled.Add, "Add to cart", Modifier.size(18.dp))
	}
}

@Preview(apiLevel = 35)
@Composable
private fun TopBarPreview() = TopSearchBar(
	remember { TextFieldState() },
	Modifier.fillMaxWidth(),
) {}

@Preview(apiLevel = 35)
@Composable
private fun ImageSliderPreview() = ImageSlider(
	listOf(
		BranchItem.MOCK,
		BranchItem.MOCK.copy(itemName = "Item 2"),
		BranchItem.MOCK.copy(itemName = "Item 3"),
		BranchItem.MOCK.copy(itemName = "Item 4"),
		BranchItem.MOCK.copy(itemName = "Item 5")
	),
	Modifier.widthIn(0.dp, 512.dp).aspectRatio(2f)
)

@Preview(apiLevel = 35, showBackground = true)
@Composable
private fun MenuTagsPreview() = MenuTags(Tag.MOCKS)

@Preview(apiLevel = 35)
@Composable
private fun CartStatusBarPreview() = CartStatusBar(
	null, CustomerRoutes.Order("branchID", "tableNumber"), listOf(
		CartEntry(BranchItem.MOCK, 2),
		CartEntry(BranchItem.MOCK.copy(itemName = "Another Item"), 1)
	)
)

@Preview(apiLevel = 35)
@Composable
private fun WideItemCardPreview() = WideItemCard(BranchItem.MOCK)

@Preview(apiLevel = 35)
@Composable
private fun TallItemCardPreview() = TallItemCard(BranchItem.MOCK)
