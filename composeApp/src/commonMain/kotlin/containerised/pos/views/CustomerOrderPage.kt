package containerised.pos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
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
import containerised.pos.models.MenuItem
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import posapplication.composeapp.generated.resources.Res
import posapplication.composeapp.generated.resources.baseline_menu_24

val mockItems = listOf(
	MenuItem(
		id = "1",
		name = "Margherita Pizza",
		description = "Classic pizza with tomato sauce, mozzarella, and basil.",
		price = 8.99f,
		categoryID = "cat1",
		isAvailable = true,
		estimatedPreparationTime = 15,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
	MenuItem(
		id = "2",
		name = "Caesar Salad",
		description = "Crisp romaine lettuce with Caesar dressing, croutons, and Parmesan cheese.",
		price = 6.49f,
		categoryID = "cat2",
		isAvailable = true,
		estimatedPreparationTime = 10,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
	MenuItem(
		id = "3",
		name = "Spaghetti Carbonara",
		description = "Spaghetti pasta with creamy sauce, pancetta, and Parmesan cheese.",
		price = 10.99f,
		categoryID = "cat1",
		isAvailable = false,
		estimatedPreparationTime = 20,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun CustomerOrderPage(navController: NavController?) {
	val padding = 16.dp
	val textFieldState = remember { TextFieldState() }
	var searchResults by remember { mutableStateOf(listOf<String>()) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.verticalScroll(rememberScrollState())
	) {
		SimpleSearchBar(
			textFieldState = textFieldState,
			onSearch = { query ->
				// Simulate search logic
				searchResults = if (query.isNotEmpty()) {
					List(10) { "Result for \"$query\" #$it" }
				} else {
					emptyList()
				}
			},
			searchResults = searchResults,
		)

		Spacer(Modifier.size(padding))

		ImageSlider()

		Spacer(Modifier.size(padding))

		Text("Popular Food", style = MaterialTheme.typography.headlineMedium)

		Spacer(Modifier.size(padding))

		LazyRow {
			items(mockItems.size) { index ->
				ItemCard(menuItem = mockItems[index])
				Spacer(Modifier.size(padding))
			}
		}

		Spacer(Modifier.size(padding))

		Text("Your search", style = MaterialTheme.typography.headlineMedium)

		LazyRow {
			items(mockItems.size) {
				InputChip(label = { Text("Tag") }, selected = false, onClick = { /*TODO*/ })
				Spacer(Modifier.size(padding))
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
			modifier = Modifier.semantics { traversalIndex = 0f },
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
							imageVector = vectorResource(Res.drawable.baseline_menu_24),
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
fun ImageSlider() {
	val pagerState = rememberPagerState(pageCount = { 5 })

	Card(Modifier.widthIn(0.dp, 512.dp).aspectRatio(2f)) { }
}

@Composable
fun ItemCard(menuItem: MenuItem) {
	val cardWidth = 128.dp

	OutlinedCard(modifier = Modifier.size(cardWidth, 256.dp)) {
		Column {
			KamelImage(
				resource = { asyncPainterResource("https://placehold.co/256x256") },
				contentDescription = menuItem.name,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(cardWidth)
					.aspectRatio(1f)
					.clip(RoundedCornerShape(8.dp)),
				onFailure = {
					Box(
						modifier = Modifier
							.size(cardWidth)
							.aspectRatio(1f)
							.clip(RoundedCornerShape(8.dp))
							.background(Color(0xFF0358AD)),
						contentAlignment = Alignment.Center
					) {}
				}
			)
			Column(modifier = Modifier.padding(8.dp)) {
				Text(
					menuItem.name,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.titleMedium
				)
				menuItem.description?.let {
					Text(
						it,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis,
						style = MaterialTheme.typography.bodyMedium
					)
				}
				Text("$${menuItem.price}", style = MaterialTheme.typography.bodyLarge)
			}
		}
	}
}

@Preview
@Composable
fun PreviewItemCard() {
	val sampleItem = MenuItem(
		id = "1",
		name = "Sample Dish",
		description = "A delicious sample dish to try out.",
		price = 9.99f,
		categoryID = "cat1",
		isAvailable = true,
		estimatedPreparationTime = 15,
		imageURL = null,
		branchID = "branch1",
		specialNotes = null,
	)

	ItemCard(menuItem = sampleItem)
}
