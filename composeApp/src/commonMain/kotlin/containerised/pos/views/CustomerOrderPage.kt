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
import containerised.pos.models.BranchItem
import containerised.pos.models.Tag
import containerised.pos.models.fetchBranchItem
import containerised.pos.models.fetchTags
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun CustomerOrderPage(navController: NavController?) {
    val padding = 16.dp
    val textFieldState = remember { TextFieldState() }
    var searchResults by remember { mutableStateOf(listOf<String>()) }

    // State for items and tags
    var items by remember { mutableStateOf<List<BranchItem>>(emptyList()) }
    var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Fetch data on startup
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                items = fetchBranchItem()
                tags = fetchTags()
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
        floatingActionButton = { CartFAB(navController) }
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

                // Popular Food Section
                item {
                    Text("Popular Food", Modifier.padding(8.dp, 0.dp), style = MaterialTheme.typography.headlineMedium)
                }

                item {
                    LazyRow(
                        Modifier.padding(8.dp, 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(padding),
                    ) {
                        items(items.size) { index ->
                            TallItemCard(item = items[index])
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
                items(items.size) { index ->
                    WideItemCard(
                        item = items[index],
                        modifier = Modifier.padding(8.dp, 0.dp)
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

@Preview
@Composable
fun CartFAB(navController: NavController?) {
	ExtendedFloatingActionButton(
		text = { Text("View Cart") },
		icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
		onClick = { navController?.navigate("checkout") },
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
                Text("$${item.price}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun WideItemCard(item: BranchItem, modifier: Modifier = Modifier, onAddToCart: () -> Unit = {}) {
    OutlinedCard(modifier = modifier.fillMaxWidth().height(120.dp)) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
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
                    Text("$${item.price}", style = MaterialTheme.typography.bodyLarge)
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

//@Preview
//@Composable
//fun PreviewTallItemCard() {
//	val sampleItem = Item(
//		id = "1",
//		name = "Sample Dish",
//		description = "A delicious sample dish to try out.",
//		price = 9.99f,
//		categoryID = "cat1",
//		isAvailable = true,
//		estimatedPreparationTime = 15,
//		imageURL = null,
//		branchID = "branch1",
//		specialNotes = null,
//	)
//
//	TallItemCard(item = sampleItem)
//}

//@Preview
//@Composable
//fun PreviewWideItemCard() {
//	val sampleItem = MenuItem(
//		id = "1",
//		name = "Sample Dish",
//		description = "A delicious sample dish to try out.",
//		price = 9.99f,
//		categoryID = "cat1",
//		isAvailable = true,
//		estimatedPreparationTime = 15,
//		imageURL = null,
//		branchID = "branch1",
//		specialNotes = null,
//	)
//
//	WideItemCard(item = sampleItem)
//}
