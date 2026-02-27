package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.CartService
import containerised.pos.models.BranchItem
import containerised.pos.models.Tag
import containerised.pos.routes.CustomerRoutes
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderPage(navController: NavController?, args: CustomerRoutes.Order) {
	val padding = 16.dp

	//	Search state
	val textFieldState = remember { TextFieldState() }
	var searchResults by remember { mutableStateOf(listOf<String>()) }

	// State for items and tags
	var featuredItems by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
	var allItems by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
	var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }

	//	Data fetching state
	var isLoading by remember { mutableStateOf(true) }
	val scope = rememberCoroutineScope()

	// Fetch data on startup
	LaunchedEffect(Unit) {
		scope.launch {
			try {
				featuredItems = BranchItem.fetchAll()
				allItems = BranchItem.fetchAll()
				tags = Tag.fetchAll()
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
			SimpleSearchBar(
				textFieldState = textFieldState,
				searchResults = searchResults,
				onSearch = { query ->
					// Simulate search logic
					searchResults = if (query.isNotEmpty()) {
						List(10) { "Result for \"$query\" #$it" }
					} else {
						emptyList()
					}
				},
			)
		},
		bottomBar = {
			BottomAppBar(
				actions = {
					Text(
						"Table ${args.tableNumber} at Branch ${args.branchID}\nTotal: $0.00",
						Modifier.padding(padding),
						style = MaterialTheme.typography.titleMedium
					)
				},
				floatingActionButton = { CartFAB(navController, args.branchID, args.tableNumber) }
			)
		}
	) { paddingValues ->
		if (isLoading) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			LazyColumn(
				modifier = Modifier.padding(paddingValues),
				verticalArrangement = Arrangement.spacedBy(padding)
			) {
				// Image Slider
				item {
					ImageSlider(Modifier.padding(8.dp))
				}

				// Featured Section
				item {
					Text("Featured", Modifier.padding(8.dp, 0.dp), style = MaterialTheme.typography.headlineMedium)
				}

				item {
					LazyRow(
						Modifier.padding(8.dp, 0.dp),
						horizontalArrangement = Arrangement.spacedBy(padding),
					) {
						items(featuredItems.size) { index ->
							TallItemCard(
								item = featuredItems[index],
								onAddToCart = {
									println("Item added to cart: ${featuredItems[index].itemName}")
									CartService.addOrIncreaseItem(featuredItems[index])
								}
							)
						}
					}
				}

				// Your search Section
				item {
					Text("Your search", Modifier.padding(8.dp, 0.dp), style = MaterialTheme.typography.headlineMedium)
				}

				item {
					LazyRow(
						Modifier.padding(8.dp, 0.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						items(tags.size) { index ->
							InputChip(
								label = { Text(tags[index].tagName) },
								selected = false,
								onClick = { /*TODO: Implement tag filtering*/ }
							)
						}
					}
				}

				// Search results items
				items(allItems.size) { index ->
					WideItemCard(
						item = allItems[index],
						modifier = Modifier.padding(8.dp, 0.dp),
						onAddToCart = { CartService.addOrIncreaseItem(allItems[index]) }
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
	textFieldState: TextFieldState,
	onSearch: (String) -> Unit,
	searchResults: List<String>,
	modifier: Modifier = Modifier
) {
	// Controls expansion state of the search bar
	var expanded by rememberSaveable { mutableStateOf(false) }

	Box(modifier.semantics { isTraversalGroup = true }) {
		SearchBar(
			modifier = Modifier
				.fillMaxWidth()
				.widthIn(32.dp, 512.dp)
				.padding(8.dp)
				.semantics { traversalIndex = 0f },
			inputField = {
				SearchBarDefaults.InputField(
					query = textFieldState.text.toString(),
					onQueryChange = { textFieldState.edit { replace(0, length, it) } },
					onSearch = {
						onSearch(textFieldState.text.toString())
						expanded = false
					},
					expanded = expanded,
					onExpandedChange = { expanded = it },
					placeholder = { Text("Search") },
					leadingIcon = {
						Icon(
							imageVector = Icons.Filled.Search,
							contentDescription = "Search Icon"
						)
					}
				)
			},
			expanded = expanded,
			onExpandedChange = { expanded = it },
		) {}
	}
}

@Composable
fun ImageSlider(modifier: Modifier) {
	val pagerState = rememberPagerState(pageCount = { 5 })

	Card(modifier.widthIn(0.dp, 512.dp).aspectRatio(2f)) { }
}

@Composable
fun CartFAB(navController: NavController?, branchID: String, tableNumber: String) {
	ExtendedFloatingActionButton(
		text = { Text("View Cart") },
		icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
		onClick = { navController?.navigate(CustomerRoutes.Checkout(branchID, tableNumber)) },
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = MaterialTheme.colorScheme.onPrimary,
	)
}

@Composable
fun TallItemCard(item: BranchItem, onAddToCart: () -> Unit = {}) {
	val cardWidth = 128.dp

	OutlinedCard(modifier = Modifier.size(cardWidth, 256.dp)) {
		Column {
			Box {
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
								.aspectRatio(1f)
								.clip(RoundedCornerShape(8.dp))
								.background(Color(0xFF0358AD)),
						) {}
					}
				)

				// Add to cart button positioned at top-right
				AddToCartButton(onAddToCart, modifier = Modifier.align(Alignment.TopEnd))
			}
			Column(modifier = Modifier.padding(8.dp)) {
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
	}
}

@Composable
fun WideItemCard(item: BranchItem, modifier: Modifier = Modifier, onAddToCart: () -> Unit = {}) {
	OutlinedCard(modifier.fillMaxWidth().height(120.dp)) {
		Box {
			Row(Modifier.fillMaxWidth()) {
				KamelImage(
					resource = { asyncPainterResource("https://placehold.co/256x256") },
					contentDescription = item.itemName,
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(120.dp)
						.clip(RoundedCornerShape(8.dp)),
					onFailure = {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier
								.size(120.dp)
								.clip(RoundedCornerShape(8.dp))
								.background(Color(0xFF0358AD)),
						) {}
					}
				)
				Column(
					modifier = Modifier.padding(8.dp),
					verticalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						item.itemName,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis,
						style = MaterialTheme.typography.titleMedium
					)
					Text("${item.price} VND", style = MaterialTheme.typography.bodyLarge)
				}
			}

			// Add to cart button positioned at top-right
			AddToCartButton(onAddToCart, modifier = Modifier.align(Alignment.TopEnd))
		}
	}
}

@Composable
fun AddToCartButton(onAddToCart: () -> Unit, modifier: Modifier = Modifier) {
	FilledTonalIconButton(
		onClick = onAddToCart,
		modifier = modifier.padding(8.dp).size(32.dp),
	) {
		Icon(
			imageVector = Icons.Filled.Add,
			contentDescription = "Add to cart",
			modifier = Modifier.size(18.dp)
		)
	}
}
