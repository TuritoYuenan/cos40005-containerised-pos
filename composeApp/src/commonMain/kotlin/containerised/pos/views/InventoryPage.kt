package containerised.pos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import containerised.pos.components.LoadingView
import containerised.pos.models.Ingredient
import containerised.pos.routes.StaffRoutes
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

private val defaultPadding = 16.dp

@Composable
fun InventoryPage(navController: NavController) {
	val scope = rememberCoroutineScope()
	var error by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
	var searchQuery by remember { mutableStateOf("") }

	suspend fun refreshInventory() {
		try {
			isLoading = true
			ingredients = Ingredient.fetchByBranch("BRA26011700").sortedBy { it.id }
			error = null
		} catch (e: Exception) {
			error = e.message
		} finally {
			isLoading = false
		}
	}

	LaunchedEffect(Unit) { scope.launch { refreshInventory() } }

//	Search
	LaunchedEffect(searchQuery) {
		ingredients = if (searchQuery.isBlank()) {
			Ingredient.fetchByBranch("BRA26011700").sortedBy { it.id }
		} else {
			Ingredient.fetchByBranch("BRA26011700")
				.filter { ingredient ->
					ingredient.ingredientName?.contains(searchQuery, ignoreCase = true) == true ||
						ingredient.unit?.contains(searchQuery, ignoreCase = true) == true
				}
				.sortedBy { it.id }
		}
	}

	val filteredIngredients = remember(ingredients, searchQuery) {
		if (searchQuery.isBlank()) {
			ingredients
		} else {
			ingredients.filter { ingredient ->
				ingredient.ingredientName?.contains(searchQuery, ignoreCase = true) == true ||
					ingredient.unit?.contains(searchQuery, ignoreCase = true) == true
			}
		}
	}

	Column(Modifier.fillMaxSize()) {
		// Search bar
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth().padding(defaultPadding),
			placeholder = { Text("Search items by name or unit...") },
			leadingIcon = { Icon(Icons.Filled.Search, "Search") },
			singleLine = true
		)

		if (isLoading) {
			LoadingView(Modifier.fillMaxSize())
			return
		}

		if (error != null) {
			Column(
				Modifier.fillMaxSize().padding(defaultPadding),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("Failed to load inventory items\n$error", color = MaterialTheme.colorScheme.error)
			}
			return
		}

		// Ingredients list
		LazyColumn(
			Modifier.padding(horizontal = defaultPadding),
			verticalArrangement = Arrangement.spacedBy(defaultPadding)
		) {
			items(filteredIngredients.size) { index ->
				IngredientCard(navController, filteredIngredients[index])
			}
		}
	}
}

@Composable
fun IngredientCard(navController: NavController, ingredient: Ingredient) {
	OutlinedCard(
		onClick = { navController.navigate(StaffRoutes.IngredientDetail(ingredient.id ?: "")) },
	) {
		Column(
			Modifier.padding(defaultPadding),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					"${ingredient.ingredientName}",
					style = MaterialTheme.typography.headlineMedium
				)

				Text(
					"${ingredient.currentStock} ${ingredient.unit} in stock",
					style = MaterialTheme.typography.titleMedium
				)
			}

			HorizontalDivider()

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Row(horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)) {
					Icon(Icons.Default.Inventory, "Info")
					Text(
						"Minimum stock level: ${ingredient.minStockLevel} ${ingredient.unit}",
						style = MaterialTheme.typography.bodyMedium
					)
				}

				Row(horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing)) {
					Icon(Icons.Default.LocalShipping, "Info")
					Text(
						formatSupplier(ingredient.supplierInfo),
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}

			HorizontalDivider()

			Row(
				Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
			) {
				Button({
					navController.navigate(StaffRoutes.StockHistory(ingredient.id ?: ""))
				}) {
					Text("View History")
				}
			}
		}
	}
}

private fun formatSupplier(info: JsonObject?): String {
	if (info == null) return "No supplier info"

	val supplierName = info["supplier"]?.jsonPrimitive?.content ?: "Unknown Supplier"
	val contact = info["contact"]?.jsonPrimitive?.content ?: "No contact info"

	return "Supplier: $supplierName ($contact)"
}

@Preview(apiLevel = 35)
@Composable
fun IngredientCardPreview() {
	IngredientCard(rememberNavController(), Ingredient.MOCK)
}
