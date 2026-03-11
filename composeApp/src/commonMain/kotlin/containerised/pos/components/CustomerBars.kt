@file:OptIn(ExperimentalMaterial3Api::class)

package containerised.pos.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CartStatusBar(
	itemCount: Int,
	totalPrice: Int,
	onViewCart: () -> Unit = {},
) = BottomAppBar(
	actions = {
		Text(
			"$itemCount items\nTotal: $totalPrice VND",
			Modifier.padding(8.dp, 0.dp),
			style = MaterialTheme.typography.titleMedium
		)
	},
	floatingActionButton = { CartFAB { onViewCart() } }
)

@Composable
fun OrderSearchBar(
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
fun CheckoutTopBar(navController: NavController?) = CenterAlignedTopAppBar(
	{ Text("My Cart") },
	navigationIcon = { BackButton { navController?.popBackStack() } },
)

@Composable
fun PaymentTopBar(onBackToHome: () -> Unit) = CenterAlignedTopAppBar(
	{ Text("Payment") },
	navigationIcon = { HomeButton { onBackToHome() } }
)

@Preview(apiLevel = 35)
@Composable
private fun CartStatusBarPreview() = CartStatusBar(3, 15000)

@Preview(apiLevel = 35)
@Composable
private fun OrderTopBarPreview() = OrderSearchBar(TextFieldState()) {}

@Preview(apiLevel = 35)
@Composable
private fun CheckoutTopBarPreview() = CheckoutTopBar(null)

@Preview(apiLevel = 35)
@Composable
private fun PaymentTopBarPreview() = PaymentTopBar { }
